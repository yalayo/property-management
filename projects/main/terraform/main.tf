terraform {
  required_providers {
    hcloud = {
      source  = "hetznercloud/hcloud"
      version = "~> 1.45"
    }
  }
}

variable "hcloud_token" {
  sensitive = true
}

variable "firewall_source_ip" {
  default = "0.0.0.0"
}

# Configure the Hetzner Cloud Provider
provider "hcloud" {
  token = "${var.hcloud_token}" 
}

## Open ports
resource "hcloud_firewall" "common-firewall" { 
  name = "common-firewall"

  rule {
    direction = "in"
    protocol  = "tcp"
    port      = "80" 
    source_ips = [
      "${var.firewall_source_ip}/32" 
    ]
  }

  rule {
    direction = "in"
    protocol  = "tcp"
    port      = "443" 
    source_ips = [
      "${var.firewall_source_ip}/32" 
    ]
  }
}

## Networking
resource "hcloud_network" "network" {
  name     = "network"
  ip_range = "10.0.0.0/16"
}

resource "hcloud_network_subnet" "network-subnet" {
  type         = "cloud"
  network_id   = hcloud_network.network.id
  network_zone = "eu-central"
  ip_range     = "10.0.1.0/24"
}

## VM
resource "hcloud_server" "immo" { 
  name        = "prod-immo"
  image       = "fedora-40"
  location    = "fsn1"
  server_type = "cax11" 
  keep_disk   = true
  ssh_keys    = ["ssh-key-1"] 
  firewall_ids = [hcloud_firewall.common-firewall.id]

  public_net {
    ipv6_enabled = true
  }

  network {
    network_id = hcloud_network.network.id
  }

  depends_on = [
    hcloud_network_subnet.network-subnet
  ]
}