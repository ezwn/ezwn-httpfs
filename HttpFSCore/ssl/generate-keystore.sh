# https://gist.github.com/Eng-Fouad/6cdc8263068700ade87e4e3bf459a988

/opt/java/jdk1.8.0_161/bin/keytool -genkeypair -keystore keystore.p12 -storetype PKCS12 -storepass httpfs2482 -alias KEYSTORE_ENTRY -keyalg RSA -keysize 2048 -validity 99999 -dname "CN=HttpFS Https, OU=Maison, O=Maison, L=Paris, ST=France, C=fr"
