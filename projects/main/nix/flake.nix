{
  description = "Clojure app Docker image with NixFS";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  };

  outputs = { self, nixpkgs }: let
    pkgsX86 = import nixpkgs { system = "x86_64-linux"; };
    pkgsAArch = import nixpkgs { system = "aarch64-linux"; };
    in {
        packages.x86_64-linux.default = pkgsX86.hello; # optional
        packages.aarch64-linux.dockerImage = pkgsAArch.dockerTools.buildImage {
            name = "property-management";
            tag = "latest";
            fromImage = "explore-bzl/nixfs:latest";
            copyToRoot = pkgsAArch.lib.cleanSource ../..;
            config = { Cmd = ["bash" "-c" "./run.sh"]; };
        };
    };
}
