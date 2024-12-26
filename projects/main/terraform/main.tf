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