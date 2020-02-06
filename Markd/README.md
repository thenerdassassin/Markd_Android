bundletool build-apks --bundle=./Desktop/josh/markd/Markd_Android/Markd/app/release/app-release.aab \
--output=./Desktop/josh/markd/Markd_Android/Markd/app/release/app-release.apks \
--ks=./Desktop/josh/markd/MarkdHomeProductionKey/AndroidKey --ks-key-alias=android_key \
--ks-pass=pass:PASSWORD --connected-device

bundletool install-apks --apks=./Desktop/josh/markd/Markd_Android/Markd/app/release/app-release.apks
