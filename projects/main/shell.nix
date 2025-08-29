{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  name = "clojure-dev-shell";

  buildInputs = [
    pkgs.jdk17      # Java JDK (required by Clojure)
    pkgs.clojure    # Clojure CLI tools (for deps.edn)
  ];

  shellHook = ''
    export JAVA_OPTS="-Xms512m -Xmx2g"
    echo "🚀 Dev shell ready with JAVA_OPTS=$JAVA_OPTS"

    export DB_PASSWORD="$POSTGRES_PASSWORD"
    export DB_HOST="$POSTGRES_HOST"

    echo "🚀 Clojure dev shell ready."
    echo "Run: clojure -M -m app.main.core"
    echo "Or start a REPL with: clojure -M:repl"
  '';
}