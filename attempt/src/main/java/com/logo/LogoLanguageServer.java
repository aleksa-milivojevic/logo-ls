package com.logo;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

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


        return CompletableFuture.supplyAsync(() -> initResult);


//        SemanticTokensWithRegistrationOptions semanticOptions = new SemanticTokensWithRegistrationOptions();
//        semanticOptions.setFull(true);
//        semanticOptions.setLegend(new SemanticTokensLegend(
//                Arrays.asList("keyword", "variable", "function"),
//                new ArrayList<>()
//        ));
//        cap.setSemanticTokensProvider(semanticOptions);
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
