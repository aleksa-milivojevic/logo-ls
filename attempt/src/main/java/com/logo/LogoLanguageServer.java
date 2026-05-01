package com.logo;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogoLanguageServer implements LanguageServer, LanguageClientAware {
    private LanguageClient client;
    private final LogoTextDocumentService textService = new LogoTextDocumentService();
    private int errorCode = 1;

    LogoLanguageServer() {
        super();
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        InitializeResult initResult = new InitializeResult(new ServerCapabilities());

        initResult.getCapabilities().setTextDocumentSync(TextDocumentSyncKind.Full);

        CompletionOptions completionOptions = new CompletionOptions();
        initResult.getCapabilities().setCompletionProvider(completionOptions);

        DeclarationRegistrationOptions declarationOptions = new DeclarationRegistrationOptions();
        initResult.getCapabilities().setDeclarationProvider(declarationOptions);

        SemanticTokensWithRegistrationOptions semanticTokens = new SemanticTokensWithRegistrationOptions();
        semanticTokens.setFull(true);
        semanticTokens.setLegend(new SemanticTokensLegend(Arrays.asList("keyword", "variable", "function", "number"), new ArrayList<>()));
        initResult.getCapabilities().setSemanticTokensProvider(semanticTokens);

        findDeclarations(Paths.get(URI.create(params.getRootUri())));

        return CompletableFuture.supplyAsync(() -> initResult);
    }

    private void findDeclarations(Path rootURI) {

        if (Files.isRegularFile(rootURI) && rootURI.endsWith(".lgo")) {

            String content = null;
            try {
                content = Files.readString(rootURI);
                String[] lines = content.split("\\r?\\n");
                Map<String, LocationLink> map = new HashMap<>();

                for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
                    String line = lines[lineIndex];
                    Matcher matcher = Pattern.compile("(?:to|define|def|make) [\":]?([a-zA-Z]+)|name [0-9]+ [\":]?([a-zA-Z]+)").matcher(line);

                    while (matcher.find()) {
                        LocationLink link = new LocationLink(
                                rootURI.toString(),
                                new Range(new Position(lineIndex, matcher.start()), new Position(lineIndex, matcher.end())),
                                new Range(new Position(lineIndex, matcher.start()), new Position(lineIndex, matcher.end()))
                        );
                        String name = (matcher.group(1) != null) ? matcher.group(1) : matcher.group(2);
                        map.put(name, link);
                        textService.addDeclaration(rootURI.toString(), map);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if(Files.isDirectory(rootURI)) {
            File dir = new File(rootURI.toString());
            File[] files = dir.listFiles();
            for (File file: files) {
                findDeclarations(file.toPath());
            }
        }
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        errorCode = 0;
        return null;
    }

    @Override
    public void exit() {
        System.exit(errorCode);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return textService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return null;
    }

    @Override
    public void connect(LanguageClient client) {
        this.client = client;
    }
}
