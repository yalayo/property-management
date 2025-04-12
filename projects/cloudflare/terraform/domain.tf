resource "cloudflare_pages_domain" "custom_domain" {
  account_id   = var.cloudflare_account_id
  project_name = cloudflare_pages_project.pages_project.name
  name         = "immo.busqandote.com"

  depends_on = [
    cloudflare_pages_project.pages_project
  ]
}

resource "cloudflare_dns_record" "dns_record" {
  zone_id = var.domain_zone_id
  content = "${cloudflare_pages_project.pages_project.name}.pages.dev"
  name = "immo"
  proxied = true
  ttl = 1
  type = "CNAME"

  depends_on = [
    cloudflare_pages_domain.custom_domain
  ]
}