{
  description = "My Clojure app run directly from source";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };
      in {
        # For development
        devShells.default = pkgs.mkShell {
          buildInputs = [
            pkgs.clojure
            pkgs.jdk17
          ];
        };

        # For running in production (without compilation)
        apps.default = {
          type = "app";
          program = "${pkgs.clojure}/bin/clojure";
          args = [ "-M" "-m" "app.main.core" ];
        };
      });
}