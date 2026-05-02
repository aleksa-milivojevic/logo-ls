package com.logo;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;

import java.util.concurrent.Future;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
public class LogoLauncher {
    public static void main(String[] args) throws IOException {

        LogoLanguageServer server = new LogoLanguageServer();

        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, System.in, System.out);

        server.connect(launcher.getRemoteProxy());

        launcher.startListening();

    }
}
