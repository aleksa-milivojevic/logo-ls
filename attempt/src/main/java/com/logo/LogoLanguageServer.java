package com.logo;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
        semanticTokens.setLegend(new SemanticTokensLegend(Arrays.asList("keyword", "variable", "function", "number", "declaration"), new ArrayList<>()));
        initResult.getCapabilities().setSemanticTokensProvider(semanticTokens);

        System.err.println(params.getRootPath());

        return CompletableFuture.supplyAsync(() -> initResult);
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
