package com.logo;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.jsonrpc.messages.Either3;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogoTextDocumentService implements TextDocumentService {
    private final LogoTokens logoTokens = new LogoTokens();
    private final Map<String, String> openDocs = new HashMap<>();
    private final Map<String, Map<String, LocationLink>> declarations = new HashMap<>();
    private final List<String> declarationNames = new ArrayList<>();


    @Override
    public CompletableFuture<SemanticTokens> semanticTokensFull (SemanticTokensParams params) {
        List<Integer> data = new ArrayList<>();

        String content = openDocs.get(params.getTextDocument().getUri());
        String[] lines = content.split("\\r?\\n");

        int lastToken = 0;
        int lastTokenLine = 0;
        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex];
            Matcher matcher = Pattern.compile("(do\\.(while|until))|[:\"]?[a-zA-Z]+|[a-zA-Z]+|[0-9]+").matcher(line);

            while (matcher.find()) {
                String token = matcher.group();
                int tokenType = getTokenType(token);
                if (tokenType == -1) continue;
                int tokenLine = lineIndex - lastTokenLine;
                int tokenIndex = (tokenLine == 0) ? matcher.start() - lastToken : matcher.start();

                data.add(tokenLine);
                data.add(tokenIndex);
                data.add(token.length());
                data.add(tokenType);
                data.add(0);

                lastToken = matcher.start();
                lastTokenLine = lineIndex;
            }
        }
        return CompletableFuture.supplyAsync(() -> new SemanticTokens(data));

    }

    private int getTokenType(String token) {
        if (token.matches("[0-9]+"))
            return 3;
        else if (token.startsWith(":") || token.startsWith("\""))
            return 1;
        else if (logoTokens.functions.contains(token))
            return 2;
        else if (logoTokens.keywords.contains(token))
            return 0;
        else if (declarationNames.contains(token))
            return 4;

        return -1;
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> declaration(DeclarationParams params) {
        String content = openDocs.get(params.getTextDocument().getUri());
        String[] lines = content.split("\\r?\\n");
        String line = lines[params.getPosition().getLine()];
        int charIndex = params.getPosition().getCharacter();

        int start = charIndex;
        while (start > 0 && line.charAt(start - 1) != ' ') start--;
        int end = charIndex;
        while (end < line.length() && line.charAt(end) != ' ') end++;
        String token = line.substring(start, end);

        List<LocationLink> list = new ArrayList<>();
        LocationLink link;
        for (Map<String, LocationLink> value: declarations.values()) {
             link = value.get(token);
             if (link != null)
                list.add(link);
        }
        return CompletableFuture.completedFuture(Either.forRight(list));
    }

    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams params) {
        List<CompletionItem> list = new ArrayList<>();
        int lineIndex = params.getPosition().getLine();
        int charIndex = params.getPosition().getCharacter();

        //List<String> text = Files.readAllLines(Paths.get(URI.create(params.getTextDocument().getUri())));
        String content = openDocs.get(params.getTextDocument().getUri());
        String[] text = content.split("\\r?\\n");
        String line = text[lineIndex];
        int endIndex = charIndex;
        while (endIndex < line.length() && line.charAt(endIndex) != ' ')
            endIndex++;
        String trigger = line.substring(charIndex, endIndex);

        for (String token : declarationNames) {
            if (token.contains(trigger)) {
                CompletionItem ci = new CompletionItem();
                ci.setLabel(token);
                ci.setKind(token.startsWith("\"") || token.startsWith(":") ? CompletionItemKind.Variable : CompletionItemKind.Function);
                list.add(ci);
            }
        }
        for (String token : logoTokens.keywords) {
            if (token.contains(trigger)) {
                CompletionItem ci = new CompletionItem();
                ci.setLabel(token);
                ci.setKind(CompletionItemKind.Keyword);
                list.add(ci);
            }
        }
        for (String token : logoTokens.functions) {
            if (token.contains(trigger)) {
                CompletionItem ci = new CompletionItem();
                ci.setLabel(token);
                ci.setKind(CompletionItemKind.Function);
                list.add(ci);
            }
        }

        return CompletableFuture.completedFuture(Either.forLeft(list));
    }

    public CompletableFuture<CompletionItem> resolveCompletionItem(CompletionItem unresolved) {
        return CompletableFuture.completedFuture(unresolved);
    }

    public void findDeclarations(String doc) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(URI.create(doc)));
            Map<String, LocationLink> map = new HashMap<>();
            int lineIndex = 0;
            for (String line : lines) {
                Matcher matcher = Pattern.compile("(?:to|define|def|make) ([\":]?[a-zA-Z]+)|name [0-9]+ ([\":]?[a-zA-Z]+)").matcher(line);

                while (matcher.find()) {
                    int targetRangeStart = matcher.group().indexOf((matcher.group(1) != null) ? matcher.group(1) : matcher.group(2));
                    LocationLink link = new LocationLink(
                            doc,
                            new Range(new Position(lineIndex, targetRangeStart), new Position(lineIndex, matcher.end())),
                            new Range(new Position(lineIndex, matcher.start()), new Position(lineIndex, matcher.end()))
                    );
                    String name = (matcher.group(1) != null) ? matcher.group(1) : matcher.group(2);
                    map.put(name, link);
                    addDeclaration(doc, map);
                }
                lineIndex += 1;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void addDeclaration(String uri, Map<String, LocationLink> map) {
        declarations.put(uri, map);
        for (String key: map.keySet())
            if (!declarationNames.contains(key))
                declarationNames.add(key);
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        openDocs.put(params.getTextDocument().getUri(), params.getTextDocument().getText());
        findDeclarations(params.getTextDocument().getUri());
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        openDocs.replace(params.getTextDocument().getUri(), params.getContentChanges().getFirst().getText());
        findDeclarations(params.getTextDocument().getUri());
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        openDocs.remove(params.getTextDocument().getUri());
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {

    }
}
