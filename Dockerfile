FROM gitpod/workspace-mysql
RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && sdk install java 11.0.12.fx-zulu"