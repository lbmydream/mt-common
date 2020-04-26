declare -A configPathMap
configPathMap["mt0-oauth2"]="mt0-oauth2"
configPathMap["mt1-proxy"]="mt1-proxy"
configPathMap["mt2-user-profile"]="mt2-user-profile"
configPathMap["mt3-product"]="mt3-product"
configPathMap["mt4-messenger"]="mt4-messenger"
configPathMap["mt5-file-upload"]="mt5-file-upload"
configPathMap["mt6-payment"]="mt6-payment"

declare -A configKeyMap
configKeyMap["mt0-oauth2"]="com.hw:oauth2"
configKeyMap["mt1-proxy"]="com.hw:proxy"
configKeyMap["mt2-user-profile"]="com.hw:user-profile"
configKeyMap["mt3-product"]="com.hw:product"
configKeyMap["mt4-messenger"]="com.hw:messenger"
configKeyMap["mt5-file-upload"]="com.hw:file-upload"
configKeyMap["mt6-payment"]="com.hw:payment"

for i in "${!configPathMap[@]}"; do
  docker run -it --rm -v ~/Apps/Public/${configPathMap[$i]}:/usr/src/app -v ~/.m2:/root/.m2 -w /usr/src/app maven:3.6.0-jdk-11 mvn clean verify sonar:sonar -Dsonar.projectKey=${configKeyMap[$i]} -Dsonar.organization=publicdevop2019-github -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=$1
done