{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  name = "clojure-dev-shell";

  buildInputs = [
    pkgs.jdk17      # Java JDK (required by Clojure)
    pkgs.clojure    # Clojure CLI tools (for deps.edn)
    pkgs.nodejs_20  # To use shadow-cljs
  ];

  shellHook = ''
    #export JAVA_OPTS="-Xms512m -Xmx2g"
    
    echo "🚀 Clojure dev shell ready."
  '';
}