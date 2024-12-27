#!/bin/bash
sudo apt-get update
sudo apt-get -y upgrade
sudo apt-get -y install apt-transport-https ca-certificates curl software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu  $(lsb_release -cs)  stable"
sudo apt-get update
sudo apt-get -y install docker-ce
sudo curl -L "https://github.com/docker/compose/releases/download/1.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
sudo usermod -aG docker ubuntu
sudo chmod 777 /var/run/docker.sock
sudo sed -i 's/^#*\s*PermitRootLogin\s.*/PermitRootLogin prohibit-password/' /etc/ssh/sshd_config
sudo systemctl restart ssh