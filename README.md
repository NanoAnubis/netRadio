# netRadio
"netRadio" is an internet radio project written in Java consisting of two separate applications. A client and a server that communicate using the UDP protocol. The server has multiple channels with different types of music that client instances can connect to, and it can stream audio packets to more than one client thanks to the use of multithreading.

# CI/CD Pipeline
## Stage 1: Style & Secret Checks
Basic style and secret leakage checks are performed against the source files of the solution.
* __Editorconfig check:__ verifies that the source files have consistent encodings, indentations, line endings, etc.
* __Gitleaks check:__ makes sure that no secrets, passwords, tokens, etc. have been committed to source control.
* __Markdown check:__ checks whether all markdown files in the solution conform to the markdown syntax.

_All checks except the markdown check should halt the pipeline if they fail._

## Stage 2: Build
The client and server applications are built with version 21 of the Maven compiler, and the build artifacts are uploaded so they can be used in later stages.

## Stage 3: Code Style & Security Checks
The build artifacts of both applications are checked to make sure they adhere to code quality and security standards.

* __Checkstyle check:__ the classes built from the source _.java_ files are checked against Sun's Java coding standards.
* __Sonarcloud check:__ the code quality and security of the built classes are analyzed using Sonracloud.
* __Snyk check:__ the build artifacts are checked for vulnerabilities using Snyk.

_All checks are performed in parallel to improve workflow performance._

## Stage 4: Docker Image Vulnerability Check
The build artifacts of the server application are used to create a Docker image, and the image is scanned for vulnerabilities using the Trivy vulnerability scanner.

## Stage 5: Upload Docker Image
If the vulnerability check of the Docker image passes, the image is uploaded to Dockerhub.

## Stage 6: Deploy to Kubernetes
Deploys the pushed Docker image to the Kubernetes cluster hosted in Azure and creates a LoadBalances service for container functionality.

__The job involves the following steps:__
1. Installs kubectl.
2. Replaces the image in the deployment file.
3. Loads KUBECONFIG from the environment variable.
4. Deploys the image to a new container.
5. Deploys a LoadBalancer service to the container.
6. Shows the External IP of the service.

__Deployment files:__
* aks-deployment.yml - Deploys a pod with the given image.
* aks-service.yml - Deploys a LoadBalancer service to the new pod.

# Terraform
Terraform is a required part for successful workflow completion. The workflow deploys a Docker image to a Kubernetes cluster that is hosted in Azure (AKS).

__Configuration files__
* providers.tf - sets the providers and connection to Terraform Cloud.
* main.tf - creates a resource group and an Azure Kubernetes Service (AKS) with one node for the deployment.
* variables.tf - some argument values in main.tf are set as variables for easier configuration.

## Requirements
1. Azure CLI installed.
2. Active Azure subsriptions.
> az login
>
> az cloud set --name AzureCloud
>
> az account set --subscription "*subscription*"
3. Permissions for creating service principals in an Azure AD.
> az ad sp create-for-rbac --role="Contributor" --scopes="/subscriptions/*subscription*"
4. Export the environment variables with the acquired information.
> export ARM\_CLIENT\_ID="*appId*"
>
> export ARM\_CLIENT\_SECRET="*password*"
>
> export ARM\_SUBSCRIPTION\_ID="*subscription*"
>
> export ARM\_TENANT\_ID="*tenant*"
5. Save the same variables in Terraform Cloud.

## Steps
1. Initiate login with Terraform Cloud - provide API token.
> terraform login
2. Initiate the directory and connect to Terraform Cloud.
> terraform init
3. Check configuration.
> terraform fmt
>
> terraform validate
4. Apply configuration.
> terraform apply
5. Get KUBECONFIG.
> terraform output kube_config
6. Save KUBECONFIG to Github variable for Kubernetes deployment.
7. Destroy infrastructure when done.
> terraform destroy

# Infrastructure as a Code - Vertical Deep Dive
Infrastructure as a Code (IaC) is a method of managing and provisioning infrastructure with configuration files rather than through a graphical user interface or custom scripts. IaC allows you to automate, standardize, and track changes to your infrastructure in a consistent and scalable way. IaC has many benefits, such as:
* __Faster speed and consistency__: IaC eliminates the need for manual approvals, reviews, and human errors, and enables faster and more reliable deployments of infrastructure.
* __Scalability and elasticity__: IaC allows you to provision and de-provision resources on demand, and adjust the infrastructure to the changing needs of your applications.
* __Cost optimization__: IaC helps you reduce waste and over-provisioning of resources, and pay only for what you use.
* __Configuration drift management__: IaC ensures that your infrastructure is always in sync with the desired state, and prevents unauthorized or accidental changes.
* __Reusability and modularity__: IaC enables you to use modules, templates, and variables to create reusable and shareable configurations that encapsulate common infrastructure patterns.

Terraform is an Infrastructure as Code (IaC) tool that allows you to define and manage your infrastructure on various cloud platforms and services using configuration files. It uses a state file to track and reconcile the current and desired state of your infrastructure, and supports modules for reusing and sharing common infrastructure patterns. Terraform Cloud is a service that provides a consistent and reliable environment for teams to collaborate on Terraform projects, with features such as shared state, access controls, policy controls, and remote operations. Terraform Cloud also has different pricing tiers to suit your needs and budget and is free for small teams.
