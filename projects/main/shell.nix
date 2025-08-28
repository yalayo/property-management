{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  name = "clojure-dev-shell";

  buildInputs = [
    pkgs.jdk17      # Java JDK (required by Clojure)
    pkgs.clojure    # Clojure CLI tools (for deps.edn)
  ];

  shellHook = ''
    echo "🚀 Clojure dev shell ready."
    echo "Run: clojure -M -m app.main.core"
    echo "Or start a REPL with: clojure -M:repl"
  '';
}