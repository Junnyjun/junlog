#! /bin/bash
KEY=$1
IV=$2

echo "KEY: $KEY"
echo "IV: $IV"

CONTAINTER_NAME="Sign-container"
IMAGE_NAME="sign-image"

echo "stop image..."
docker stop $CONTAINTER_NAME
docker rm $CONTAINTER_NAME
docker rmi $IMAGE_NAME

echo "Building image..."
docker build \
-t junny \
--build-arg AUTH=$KEY \
--build-arg IV="$IV" \
.

echo "Running image..."
docker run \
-d \
--name $CONTAINTER_NAME \
-p 8080:8080 \
$IMAGE_NAME



