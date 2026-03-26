
# Running the Application on Minikube (Windows)

## 1. Start Xming on Windows
Open PowerShell and run:
```powershell
PS C:\Program Files (x86)\Xming> .\Xming.exe :0 -ac -multiwindow -clipboard
````

## 2. Start Minikube

```powershell
minikube start
```

> Note: To run Minikube in IntelliJ, make sure it has its own Docker daemon.

## 3. Set up Docker Environment

```powershell
minikube -p minikube docker-env --shell powershell | Invoke-Expression
```

## 4. Build the Docker Image

```powershell
docker build -t taifjalo1/otp2-fuel-calculator-localization:latest .
```

> Or use your own image name and tag:

```powershell
docker build -t <your-image-name>:<tag> .
```

## 5. Deploy to Kubernetes

Apply the deployment YAML:

```powershell
kubectl apply -f fuelconsumption_deployment.yaml
```

Check Pod status:

```powershell
kubectl get pods
```

> Make sure `imagePullPolicy` in your YAML is set to:

```yaml
imagePullPolicy: Never
```

Expected Pod status:

| READY | STATUS  | RESTARTS |
| ----- | ------- | -------- |
| 1/1   | Running | 0        |

## 6. Delete Pods or Minikube

To delete a specific Pod:

```powershell
kubectl delete pod <pod-name>
```

Example:

```powershell
kubectl delete pod fuelconsumption-app-5984c69657-958w6
```