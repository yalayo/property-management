{
  description = "Clojure app Docker image with NixFS";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  };

  outputs = { self, nixpkgs }: 
    let
      system = "aarch64-linux";
      pkgs = import nixpkgs { inherit system; };
    in
    {
      dockerImage = pkgs.dockerTools.buildImage {
        name = "property-management";
        tag = "latest";

        # Use NixFS docker image as the base
        fromImage = "explore-bzl/nixfs:latest";  

        # Copy your Clojure app into the image
        copyToRoot = pkgs.lib.cleanSource ../..;  

        config = {
            Cmd = [
            "bash" "-c" ''
                ./run.sh
            ''
            ];
        };
        };
    };
}
