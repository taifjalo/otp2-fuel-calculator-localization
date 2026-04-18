pipeline {
    agent any
    environment {
        PATH = "C:\\Program Files\\Docker\\Docker\\resources\\bin;${env.PATH}"
        JAVA_HOME = 'C:\\Program Files\\Java\\jdk-21'
        DOCKERHUB_CREDENTIALS_ID = 'Docker_Hub'
        DOCKERHUB_REPO = 'taifjalo1/otp2-fuel-calculator-localization'
        DOCKER_IMAGE_TAG = 'latest'
    }
    stages {

        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/taifjalo/otp2-fuel-calculator-localization.git'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean install -DskipTests'
            }
        }

        stage('Test & Coverage') {
            steps {
                bat 'mvn test jacoco:report'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    bat """
                        mvn sonar:sonar ^
                        -Dsonar.projectKey=fuel_calculator ^
                        -Dsonar.projectName=fuel-calculator ^
                        -Dsonar.host.url=http://localhost:9000 ^
                        -Dsonar.token=squ_cc4d7e0e46844ca2f69e175476c27f10a462f98b ^
                        -Dsonar.java.binaries=target/classes ^
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    """
                }
            }
        }

        stage('Build Docker Image') {
                    steps {
                        script {
                            docker.build("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}")
                        }
                    }
                }

                stage('Push Docker Image to Docker Hub') {
                    steps {
                        script {
                            docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS_ID) {
                                docker.image("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}").push()
                            }
                        }
                    }
                }

    }
}