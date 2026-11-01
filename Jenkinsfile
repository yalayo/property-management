// CI for property-management: build + push the image with a unique tag, then hand the tag to GitOps.
// Deployment is done by Argo CD, which syncs the cluster from the GitOps repo.
pipeline {
  agent any

  environment {
    AWS_REGION            = 'us-east-1'
    AWS_DEFAULT_REGION    = 'us-east-1'
    AWS_ENDPOINT_URL      = 'http://floci:4566'
    AWS_ACCESS_KEY_ID     = 'test'
    AWS_SECRET_ACCESS_KEY = 'test'

    // ECR repository (Floci) - jib pushes straight to Floci's registry container
    REPO_NAME             = 'property-management'
    ECR_REGISTRY          = 'floci-aws-ecr-registry:5000'
    IMAGE_PATH            = '000000000000/us-east-1/property-management'

    // GitOps repo (no https:// prefix - the token is inserted in front of it)
    GITOPS_REPO           = 'github.com/yalayo/gitops.git'
    GITOPS_BRANCH         = 'main'
    GITOPS_VALUES         = 'envs/floci/values.yaml'   // must contain:  tag: "..." # jenkins-image-tag
  }

  options {
    disableConcurrentBuilds()
  }

  stages {
    stage('Build & push image') {
      steps {
        script {
          env.TAG   = env.GIT_COMMIT.take(7)
          env.IMAGE = "${env.ECR_REGISTRY}/${env.IMAGE_PATH}:${env.TAG}"
          echo "Building ${env.IMAGE}"
        }
        dir('projects/main') {
          // Override jib.edn's :target-image so the tag is part of the pushed image name
          sh """
              clojure -T:jib jibbit.core/build \\
                :target-image '{:image-name "${env.IMAGE}" :type :registry :username "AWS" :password "test"}' \\
                :allow-insecure-registries true
          """
        }
      }
    }

    stage('Verify image in ECR') {
      steps {
        // Never point GitOps at a tag the cluster can't pull
        sh '''
          for i in 1 2 3 4 5; do
            if aws ecr describe-images --repository-name "$REPO_NAME" --image-ids imageTag="$TAG" >/dev/null; then
              echo "Found $REPO_NAME:$TAG in ECR"
              exit 0
            fi
            sleep 3
          done
          echo "Tag $TAG not found. Tags currently in ECR:"
          aws ecr list-images --repository-name "$REPO_NAME" --query "imageIds[].imageTag" --output text
          exit 1
        '''
      }
    }

    stage('Promote to GitOps (floci)') {
      steps {
        // Jenkins credential "github-token" (Username with password):
        //   username = your GitHub user, password = PAT with Contents read/write on the GitOps repo
        withCredentials([usernamePassword(credentialsId: 'github-token',
                                          usernameVariable: 'GIT_USER',
                                          passwordVariable: 'GIT_TOKEN')]) {
          sh '''
            set -eu
            rm -rf gitops
            git clone --branch "$GITOPS_BRANCH" "https://${GIT_USER}:${GIT_TOKEN}@${GITOPS_REPO}" gitops
            cd gitops

            # Fail loudly if the env file has no marked tag line
            grep -q '# jenkins-image-tag' "$GITOPS_VALUES" || {
              echo "No '# jenkins-image-tag' line in $GITOPS_VALUES"; exit 1; }

            # Replace the tag on the line marked "# jenkins-image-tag"
            sed -i -E 's|^([[:space:]]*tag:[[:space:]]*).*(# jenkins-image-tag.*)$|\\1"'"$TAG"'" \\2|' "$GITOPS_VALUES"

            if git diff --quiet -- "$GITOPS_VALUES"; then
              echo "Tag $TAG is already in $GITOPS_VALUES - nothing to promote"
              exit 0
            fi
            git diff -- "$GITOPS_VALUES"

            git -c user.name="jenkins" -c user.email="jenkins@localhost" \
                commit -m "property-management(floci): deploy $TAG" -- "$GITOPS_VALUES"

            # Retry if someone else pushed in the meantime
            n=0
            until git push origin "HEAD:$GITOPS_BRANCH"; do
              n=$((n + 1))
              [ "$n" -ge 3 ] && { echo "push failed 3 times"; exit 1; }
              git pull --rebase origin "$GITOPS_BRANCH"
            done
          '''
        }
      }
    }
  }

  post {
    success {
      echo "Promoted ${env.TAG}. Argo CD syncs it within ~1 min: http://argocd.localhost:8080"
    }
    always {
      sh 'rm -rf gitops' // don't leave the token-bearing clone in the workspace
    }
  }
}