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

        # Include your app source code
        contents = [
          ./                   # your Clojure app source
          ./nixfs              # path to nixfs repo/code
        ];

        # Install required packages for your app + nixfs
        config = {
          Env = {
            PATH = "/bin:/usr/bin:/usr/local/bin";
          };

          Cmd = [
            "bash" "-c" ''
              # Mount nixfs at /nix/store
              mkdir -p /nix/store
              ./nixfs/nixfs --mount /nix/store &

              # Run your Clojure app
              ./run.sh
            ''
          ];
        };

        # Optional: install dependencies inside the image
        extraCommands = ''
          ${pkgs.bash}/bin/bash --version
          ${pkgs.openjdk}/bin/java -version
          ${pkgs.curl}/bin/curl --version
        '';
      };
    };
}
