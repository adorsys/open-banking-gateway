set -x
id=$(docker container ls -a --no-trunc | grep fintech-server:1 | awk '{print $1}')
if [[ $id != "" ]]
then
        echo "container exisitiert $id"
	docker container stop $id
        docker container rm $id
fi
docker run --hostname=fintechserver --name="fintech-server" -p 8086:8086 -e JAR_FILE=fintech-server-0.0.6-SNAPSHOT.jar -t fintech-server:1
