variable "resource_group_name" {
  default = "pissRG"
}

variable "resource_group_location" {
  default = "westeurope"
}

variable "aks_cluster_name" {
  default = "pissAKS"
}

variable "aks_dns_prefix" {
  default = "pissAKS"
}

variable "aks_cluster_node_size" {
  default = "Standard_B2s"
}

variable "aks_cluster_node_count" {
  default = 1
}
