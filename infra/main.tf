terraform {
  // aws 라이브러리 불러옴
  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
  }
}

# AWS 설정 시작
provider "aws" {
  region = var.region
  profile = "my-new-account"
  default_tags {
    tags = {
      Team = "devcos5-team02"
    }
  }
}
# AWS 설정 끝


# VPC 설정 시작
resource "aws_vpc" "vpc_1" {
  cidr_block = "10.0.0.0/16"

  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "${var.prefix}-vpc-1"
  }
}

# Subnet 설정 시작
resource "aws_subnet" "subnet_1" {
  vpc_id                  = aws_vpc.vpc_1.id
  cidr_block              = "10.0.0.0/24"
  availability_zone       = "${var.region}b"
  map_public_ip_on_launch = true

  tags = {
    Name = "${var.prefix}-subnet-2"
  }
}


resource "aws_internet_gateway" "igw_1" {
  vpc_id = aws_vpc.vpc_1.id

  tags = {
    Name = "${var.prefix}-igw-1"
  }
}

resource "aws_route_table" "rt_1" {
  vpc_id = aws_vpc.vpc_1.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw_1.id
  }

  tags = {
    Name = "${var.prefix}-rt-1"
  }
}

resource "aws_route_table_association" "association_1" {
  subnet_id      = aws_subnet.subnet_1.id
  route_table_id = aws_route_table.rt_1.id
}

resource "aws_security_group" "sg_1" {
  name   = "${var.prefix}-sg-1"
  vpc_id = aws_vpc.vpc_1.id

  # 추후에 IP 제한
  ingress {
    description = "SSH Access Open"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTPS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # 추후에 IP 제한
  ingress {
    description = "NPM Admin UI Port 81"
    from_port   = 81
    to_port     = 81
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "SpringBoot App 8080"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "MySQL 3306"
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # VPC 내부에서만
  }

  ingress {
    description = "Redis 6379"
    from_port   = 6379
    to_port     = 6379
    protocol    = "tcp"
    cidr_blocks = ["10.0.0.0/16"] # VPC 내부에서만
  }

  ingress {
    description = "Grafana (for monitoring)"
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # 또는 제한된 IP 대역
  }

  # Prometheus
  ingress {
    description = "Prometheus UI"
    from_port   = 9090
    to_port     = 9090
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Node Exporter (Prometheus가 수집 가능하게)
  ingress {
    description = "Node Exporter"
    from_port   = 9100
    to_port     = 9100
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Influx DB
  ingress {
    description = "InfluxDB (for k6 test result write)"
    from_port   = 8086
    to_port     = 8086
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # 또는 제한된 IP (예: 테스트 머신의 IP)
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.prefix}-sg-1"
  }
}

# EC2 역할 생성
resource "aws_iam_role" "ec2_role_1" {
  name = "${var.prefix}-ec2-role-1"

  # 이 역할에 대한 신뢰 정책 설정. EC2 서비스가 이 역할을 가정할 수 있도록 설정
  assume_role_policy = <<EOF
  {
    "Version": "2012-10-17",
    "Statement": [
      {
        "Sid": "",
        "Action": "sts:AssumeRole",
        "Principal": {
            "Service": "ec2.amazonaws.com"
        },
        "Effect": "Allow"
      }
    ]
  }
  EOF
}

# EC2 역할에 AmazonS3FullAccess 정책을 부착
resource "aws_iam_role_policy_attachment" "s3_full_access" {
  role       = aws_iam_role.ec2_role_1.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3FullAccess"
}

# EC2 역할에 AmazonEC2RoleforSSM 정책을 부착
resource "aws_iam_role_policy_attachment" "ec2_ssm" {
  role       = aws_iam_role.ec2_role_1.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2RoleforSSM"
}

# IAM 인스턴스 프로파일 생성
resource "aws_iam_instance_profile" "instance_profile_1" {
  name = "${var.prefix}-instance-profile-1"
  role = aws_iam_role.ec2_role_1.name
}

# 추후에 설치 하는거에 따라 기초 데이터 설정
locals {
  ec2_user_data_base = <<-END_OF_FILE
#!/bin/bash
# 1. 스왑 메모리 설정
sudo dd if=/dev/zero of=/swapfile bs=128M count=32
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
sudo sh -c 'echo "/swapfile swap swap defaults 0 0" >> /etc/fstab'

# 2. Docker 설치
yum install docker -y
systemctl enable docker
systemctl start docker

# 3. Docker Compose 설치
curl -L "https://github.com/docker/compose/releases/download/v2.24.5/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# 4. Git 설치
yum install git -y

# 5. 작업 디렉토리 생성 및 레포지토리 클론
mkdir -p /app/pickgo
cd /app/pickgo
git clone https://${var.github_username}:${var.github_token}@github.com/prgrms-web-devcourse-final-project/WEB4_5_ServerSOS_BE.git .

# 6. Backend 디렉토리 이동
cd backend

# 7. DockerHub 또는 GHCR 로그인
echo "${var.github_token}" | docker login ghcr.io -u ${var.github_username} --password-stdin

END_OF_FILE
}


data "aws_ami" "latest_amazon_linux" {
  most_recent = true
  owners = ["amazon"]

  filter {
    name = "name"
    values = ["al2023-ami-2023.*-x86_64"]
  }

  filter {
    name = "architecture"
    values = ["x86_64"]
  }

  filter {
    name = "virtualization-type"
    values = ["hvm"]
  }

  filter {
    name = "root-device-type"
    values = ["ebs"]
  }
}

# EC2 인스턴스 정의
resource "aws_instance" "ec2" {
  # 개수
  count = 1
  # 사용할 AMI ID
  ami = "ami-0eb302fcc77c2f8bd"
  # EC2 인스턴스 유형
  instance_type = "t3.small"
  # 사용할 서브넷 ID
  subnet_id = aws_subnet.subnet_1.id
  # 적용할 보안 그룹 ID
  vpc_security_group_ids = [aws_security_group.sg_1.id]
  # 퍼블릭 IP 연결 설정
  associate_public_ip_address = true

  # 인스턴스에 IAM 역할 연결
  iam_instance_profile = aws_iam_instance_profile.instance_profile_1.name

  # 인스턴스에 태그 설정
  tags = {
    Name = "${var.prefix}-ec2-${count.index + 1}"
  }

  # 루트 볼륨 설정
  root_block_device {
    volume_type = "gp3"
    volume_size = 40
  }

  user_data = <<-EOF
${local.ec2_user_data_base}
EOF
}

# 개발용 버킷
resource "aws_s3_bucket" "pickgo_dev_bucket" {
  bucket        = "${var.prefix}-pickgo-dev-bucket"
  force_destroy = true

  tags = {
    Name        = "${var.prefix}-pickgo-dev-bucket"
    Environment = "dev"
  }
}

resource "aws_s3_bucket_public_access_block" "pickgo_dev_block" {
  bucket = aws_s3_bucket.pickgo_dev_bucket.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

# 운영용 버킷
resource "aws_s3_bucket" "pickgo_prod_bucket" {
  bucket        = "${var.prefix}-pickgo-prod-bucket"

  tags = {
    Name        = "${var.prefix}-pickgo-prod-bucket"
    Environment = "prod"
  }
}

resource "aws_s3_bucket_public_access_block" "pickgo_prod_block" {
  bucket = aws_s3_bucket.pickgo_prod_bucket.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_policy" "prod_bucket_policy" {
  bucket = aws_s3_bucket.pickgo_prod_bucket.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Sid       = "AllowPublicRead"
      Effect    = "Allow"
      Principal = "*"
      Action    = ["s3:GetObject"]
      Resource  = ["${aws_s3_bucket.pickgo_prod_bucket.arn}/*"]
    }]
  })
}

resource "aws_s3_bucket_policy" "dev_bucket_policy" {
  bucket = aws_s3_bucket.pickgo_dev_bucket.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Sid       = "AllowPublicRead"
      Effect    = "Allow"
      Principal = "*"
      Action    = ["s3:GetObject"]
      Resource  = ["${aws_s3_bucket.pickgo_dev_bucket.arn}/*"]
    }]
  })
}

resource "aws_instance" "metric_server" {
  ami                         = "ami-0eb302fcc77c2f8bd"
  instance_type               = "t3.micro"
  subnet_id                   = aws_subnet.subnet_1.id
  vpc_security_group_ids = [aws_security_group.sg_1.id]
  associate_public_ip_address = true
  iam_instance_profile        = aws_iam_instance_profile.instance_profile_1.name

  tags = {
    Name = "${var.prefix}-metric-server"
  }

  root_block_device {
    volume_type = "gp3"
    volume_size = 12
  }

  user_data = <<-EOF
${local.metric_server_user_data}
EOF
}

resource "aws_eip" "metric_server_eip" {
  instance = aws_instance.metric_server.id

  tags = {
    Name = "${var.prefix}-metric-server-eip"
  }
}

locals {
  metric_server_user_data = <<-END_OF_FILE
#!/bin/bash
# 1. 스왑 메모리 설정
sudo dd if=/dev/zero of=/swapfile bs=128M count=32
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
sudo sh -c 'echo "/swapfile swap swap defaults 0 0" >> /etc/fstab'

# 2. Docker 설치
yum install docker -y
systemctl enable docker
systemctl start docker

# 3. Docker Compose 설치
curl -L "https://github.com/docker/compose/releases/download/v2.24.5/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# 4. Git 설치
yum install git -y

# 5. 작업 디렉토리 생성 및 레포지토리 클론
mkdir -p /app/monitoring
cd /app/monitoring

END_OF_FILE
}