pipeline {
    agent any

    environment {        
        BUILD_MODE="prod"

        APP_PORT=8888
        APP_CONTAINER_PORT=8080
        
        POSTGRESQL_CRED = credentials('postgres-id')
        DB_SERVER="postgresql:5432"
        DB_USERNAME="${POSTGRESQL_CRED_USR}"
        DB_PASSWORD="${POSTGRESQL_CRED_PSW}"
        
        EMAIL_CRED = credentials('UTFPRAPPSEmailAndPassword')
        MAIL_USERNAME="${EMAIL_CRED_USR}"
        MAIL_PASSWORD="${EMAIL_CRED_PSW}"
        MAIL_HOST="smtp.utfpr.edu.br"
        MAIL_PORT=587

        API_HOST_URL="siacoes.app.pb.utfpr.edu.br"        
    }

    stages {   
        stage('Build via Docker Compose') {
            steps {
                sh 'docker compose up -d --build'
            }
        }
    }
}
