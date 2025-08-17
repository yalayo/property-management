{
  description = "Clojure app Docker image with NixFS";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  };

  outputs = { self, nixpkgs }: let
    pkgsX86 = import nixpkgs { system = "x86_64-linux"; };
    pkgsAArch = import nixpkgs { 
      system = "aarch64-linux"; 
      crossSystem = { config = "aarch64-linux"; }; 
    };
  in {
    packages.x86_64-linux.default = pkgsX86.hello; # optional

    # NixFS Docker image
    packages.aarch64-linux.nixfsImage = pkgsAArch.dockerTools.buildImage {
      name = "explore-bzl/nixfs";
      tag = "latest";

      # Build root filesystem with packages + custom file
      copyToRoot = pkgsAArch.runCommand "nixfs-root" { } ''
        mkdir -p $out/bin

        # Copy standard packages
        cp -r ${pkgsAArch.buildEnv {
          name = "rootfs";
          paths = [
            pkgsAArch.python310Full
            pkgsAArch.python310Packages.fusepy
            pkgsAArch.python310Packages.urllib3
            pkgsAArch.fuse
            pkgsAArch.cacert
          ];
        }}/* $out/

        # Copy custom file
        cp ${./rootfs/bin/nixfs.py} $out/bin/storefs
        sed -i 's/\r$//' $out/bin/storefs
        chmod +x $out/bin/storefs
      '';

      config = {
        Env = [
          "SSL_CERT_FILE=${pkgsAArch.cacert}/etc/ssl/certs/ca-bundle.crt"
          "PYTHONPATH=${pkgsAArch.python310Packages.fusepy}/${pkgsAArch.python310Packages.urllib3}/lib/python3.10/site-packages"
        ];
        Entrypoint = [ "/bin/storefs" ];
      };
    };

    # Property-management Docker image built on top of NixFS
    packages.aarch64-linux.dockerImage = pkgsAArch.dockerTools.buildImage {
      name = "property-management";
      tag = "latest";

      fromImage = self.packages.aarch64-linux.nixfsImage;

      copyToRoot = pkgsAArch.runCommand "property-management-root" { } ''
        mkdir -p $out
        cp -r ${pkgsAArch.lib.cleanSource ../..}/* $out/

        # Fix line endings for scripts
        find $out -type f -name '*.sh' -o -name '*.py' | xargs sed -i 's/\r$//'
        find $out -type f -name '*.sh' -o -name '*.py' | xargs chmod +x
      '';

      config = {
        # JVM command to start your Clojure app
        Cmd = [
            "java"
            "-Duser.language=de"
            "-Duser.country=DE"
            "-Xms3g"
            "-Xmx4g"
            "-Xlog:gc"
            "-XX:+UseG1GC"
            "-cp"
            "/app/classes:/app/dependency/*"
            "app.main.core"   # your main Clojure class
        ];

        Env = [
            "LANG=de_DE.UTF-8"
            "LC_ALL=de_DE.UTF-8"
        ];
      };
    };
  };
}