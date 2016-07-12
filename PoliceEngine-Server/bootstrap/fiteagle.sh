#!/usr/bin/env bash
#TODO: change screen command to screen -dmS session_name sh -c '/path/to/script.sh; exec bash' (to see errors)

_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
_base="$(pwd)"
_resources_url="https://raw.githubusercontent.com/FITeagle/bootstrap/master/resources"
_osco_url="https://svnsrv.fokus.fraunhofer.de/svn/cc/ngni/OpenSDNCore/orchestrator/branches/wildfly-branch"
[[ "$OSTYPE" == "darwin"* ]] && _isOSX=1

_bootstrap_res_folder="${_base}/bootstrap/resources/sesame"
_sesame_workbench_url="http://search.maven.org/remotecontent?filepath=org/openrdf/sesame/sesame-http-workbench/2.8.6/sesame-http-workbench-2.8.6.war"
_sesame_server_url="http://search.maven.org/remotecontent?filepath=org/openrdf/sesame/sesame-http-server/2.8.6/sesame-http-server-2.8.6.war"

_ft2_install_war="org.fiteagle.north:sfa:0.1-SNAPSHOT \
	org.fiteagle.core:reservation:0.1-SNAPSHOT \
	org.fiteagle.core:bus:1.0-SNAPSHOT \
	org.fiteagle.core:orchestrator:0.1-SNAPSHOT \
	org.fiteagle.adapters:motor:0.1-SNAPSHOT \
	org.fiteagle.adapters:networking:0.1-SNAPSHOT \
	org.fiteagle.core:federationManager:0.1-SNAPSHOT \
	org.fiteagle:native:0.1-SNAPSHOT \
	org.fiteagle.core:resourceAdapterManager:0.1-SNAPSHOT \
	"
_ft2_install_extra_war="org.fiteagle.adapters:openstack:0.1-SNAPSHOT"

_labwiki_folder="${_base}/server"
_labwiki_root="${_labwiki_folder}/labwiki"
_labwiki_git_url="https://github.com/mytestbed/labwiki.git"


_xmpp_type="openfire"
_xmpp_version="3_8_2"
#_xmpp_version="3_9_1"
_xmpp_file="${_xmpp_type}_${_xmpp_version}.tar"
_xmpp_folder="${_base}/server"
_xmpp_url="http://download.igniterealtime.org/${_xmpp_type}/${_xmpp_file}.gz"
_xmpp_config="openfire.xml"
_xmpp_config_path="conf"
_xmpp_config_url="${_resources_url}/${_xmpp_type}/${_xmpp_config_path}/${_xmpp_config}"
_xmpp_db_props="openfire.properties"
_xmpp_db_props_path="embedded-db"
_xmpp_db_props_url="${_resources_url}/${_xmpp_type}/${_xmpp_db_props_path}/${_xmpp_db_props}"
_xmpp_db_values="openfire.script"
_xmpp_db_values_path="embedded-db"
_xmpp_db_values_url="${_resources_url}/${_xmpp_type}/${_xmpp_db_values_path}/${_xmpp_db_values}"
_xmpp_keystore="keystore"
_xmpp_keystore_path="resources/security"
_xmpp_keystore_url="${_resources_url}/${_xmpp_type}/${_xmpp_keystore_path}/${_xmpp_keystore}"
_xmpp_root="${_xmpp_folder}/${_xmpp_type}"

_container_type="wildfly"
#_container_version="8.2.0.Final"
_container_version="9.0.1.Final"
_container_init_config="standalone-fiteagle.txt"
_container_name="${_container_type}-${_container_version}"
_container_file="${_container_name}.zip"
_container_url="http://download.jboss.org/${_container_type}/${_container_version}/${_container_file}"
_container_folder="${_base}/server"
_container_config="standalone-fiteagle.xml"
_container_config_url="${_resources_url}/wildfly/standalone/configuration/standalone-fiteagle.xml"
_container_root="${_container_folder}/${_container_type}"
_container_keystore="jetty-ssl.keystore"
_container_keystore_url="${_resources_url}/wildfly/standalone/configuration/jetty-ssl.keystore"
_container_truststore="jetty-ssl.truststore"
_container_truststore_url="${_resources_url}/wildfly/standalone/configuration/jetty-ssl.truststore"
_container_index="index.html"
_container_index_url="${_resources_url}/wildfly/welcome-content/${_container_index}"
_container_css="fiteagle.css"
_container_css_url="${_resources_url}/wildfly/welcome-content/${_container_css}"
_container_jQuery="jquery.js"
_container_jQuery_url="${_resources_url}/wildfly/welcome-content/${_container_jQuery}"
_container_mimic="mimic.js"
_container_mimic_url="${_resources_url}/wildfly/welcome-content/${_container_mimic}"
_container_bg="fiteagle_bg.jpg"
_container_bg_url="${_resources_url}/wildfly/welcome-content/${_container_bg}"
_container_logo="fiteagle_logo.png"
_container_logo_url="${_resources_url}/wildfly/welcome-content/${_container_logo}"
_container_standalone_deployments="${_base}/server/wildfly/standalone/deployments/"
_container_standalone_config="${_base}/server/wildfly/bin/"

_installer_folder="${_base}/tmp"
_logfile="${_installer_folder}/log"

_wildfly_admin_user="admin"
_wildfly_admin_pwd="admin"
_wildfly_app_user="fiteagle"
_wildfly_app_pwd="fiteagle"
_wildfly_app_group="guest"

runcmd() {
        echo "running: $*"
        $*
}

fold_begin () {
  if [[ -n ${TRAVIS_BUILD_DIR} ]]; then
    echo -en "travis_fold:start:${1}\r"
    if [ "x${2}" = "x" ]; then
      echo "${1}: "
    else
      echo "${2}: "
    fi
  fi
}

fold_end () {
  if [[ -n ${TRAVIS_BUILD_DIR} ]]; then
    echo -en "travis_fold:end:${1}\r"
  fi
}


function checkBinary {
  echo -n " * Checking for '$1'..."
  if command -v $1 >/dev/null 2>&1; then
     echo "OK."
     return 0
   else
     echo >&2 "FAILED."
     return 1
   fi
}

function checkDirectory {
  echo -n " * Checking for '${1}' folder..."
  if [ -d ${2} ] >/dev/null 2>&1; then
     echo "OK."
     return 0
   else
     echo >&2 "FAILED (directory does not exist!)."
     return 1
   fi
}

function checkPackage {
  echo -n " * Checking for '$1'..."
  PKG_OK=$(dpkg-query -W -f='${Status}' $1 2>/dev/null | grep -c "ok installed")
  if [ $PKG_OK -eq 1 ]; then
     echo "OK"
     return 0
   else
     echo >&2 "FAILED."
     return 1
   fi
}

function checkRubyVersion {
  echo -n " * Checking ruby version..."
  VERSION_OK=$(command -v ruby 2>/dev/null | grep -c "1.9.3")
  if [ $VERSION_OK -eq 1 ]; then
     echo "OK"
     return 0
   else
     echo >&2 "FAILED. Run \"./bootstrap/fiteagle.sh installRuby\" to install the correct version"
     return 1
   fi
}

#binDeploy-*
#deployBin-*
#ex: deployBin-org.fiteagle.adapters:networking:0.1-SNAPSHOT
function deployBin() {
  if [ -z $1 ]; then
    echo "ERROR: artefact id is null"
    exit 1
  else
    _warfile=$(echo $1|cut -d: -f2)
    rm ${_base}/server/wildfly/standalone/deployments/${_warfile}.war.failed 2>/dev/null
    ${_base}/bootstrap/bin/nxfetch.sh -n -i $1 -r fiteagle -p war -o ${_base}/server/wildfly/standalone/deployments
    return $?
  fi
}

function buildDemoDocker() {
	echo "(Re)building demo Docker image..."
	(checkBinary docker && checkBinary sudo) || (echo "please install missing binaries."; exit 1)
	_docker_path="${_base}/bootstrap/docker/"
	[[ -d "docker" && -e "docker/Dockerfile" ]] && _docker_path="./docker/"

	sudo docker build $1 --rm --no-cache --tag=fiteagle2bin ${_docker_path}
	echo "Done"
	echo 'Now start container (e.g. sudo docker run -d --name=ft2 -p 8443:8443 -p 9990:9990 --env "WILDFLY_ARGS=-bmanagement 0.0.0.0" fiteagle2bin)'
}

function buildDevDocker() {
  echo "(Re)building Development Docker image..."
  (checkBinary docker && checkBinary sudo) || (echo "please install missing binaries."; exit 1)
  _docker_path="${_base}/bootstrap/"

  sudo docker build $1 --rm --no-cache --tag=fiteagle2bin ${_docker_path}
  echo "Done"
  echo 'Now start container (e.g. sudo docker run -d --name=ft2 -p 8443:8443 -p 9990:9990 --env "WILDFLY_ARGS=-bmanagement 0.0.0.0" fiteagle2bin)'
}

function deploySesame() {
	echo "Downloading openrdf seasame & workbench..."
	[ ! -f "${_base}/server/wildfly/standalone/deployments/openrdf-sesame.war" ] &&
		curl -fsSSkL -o "${_base}/server/wildfly/standalone/deployments/openrdf-sesame.war" "${_sesame_server_url}"
	[ ! -f "${_base}/server/wildfly/standalone/deployments/openrdf-workbench.war" ] &&
		curl -fsSSkL -o "${_base}/server/wildfly/standalone/deployments/openrdf-workbench.war" "${_sesame_workbench_url}"

    if [ "${_isOSX}" ]; then
    	mkdir -p "${_base}/server/sesame/OpenRDF Sesame"
        sesame_db="${_base}/server/sesame/OpenRDF Sesame"
    else
       	mkdir -p "${_base}/server/sesame/openrdf-sesame"
        sesame_db="${_base}/server/sesame/openrdf-sesame"
  	fi
  	echo "Installing database..."
		[ ! -d "${sesame_db}/conf" ] &&
			cp -r "${_bootstrap_res_folder}/openrdf-sesame/"* "${sesame_db}/"
}

function deployBinaryOnly() {
	[ ! -d ".git" ] || { echo "Do not bootstrap within a repository"; exit 4; }

	(checkBinary git && checkBinary java && checkBinary curl) || (echo "please install missing binaries."; exit 1)

    installFITeagleModule bootstrap

    installContainer
    configContainer
    echo "Downloading binary components from repository..."
    for component in ${_ft2_install_war}; do
      deployBin ${component} || return $?
    done

    deploySesame
    echo "binary-only deployment DONE."
}

function deployExtraBinary() {
	[ ! -d ".git" ] || { echo "Do not bootstrap within a repository"; exit 4; }

	(checkBinary git && checkBinary java && checkBinary curl) || (echo "please install missing binaries."; exit 1)

    echo "Downloading binary components from repository..."
    for component in ${_ft2_install_extra_war}; do
      deployBin ${component} || return $?
    done
    echo "extra binary-only deployment DONE."
}


function installXMPP() {
    echo "Downloading XMPP server..."
    mkdir -p "${_installer_folder}"
    [ -f "${_installer_folder}/${_xmpp_file}" ] || curl -fsSSkL -o "${_installer_folder}/${_xmpp_file}" "${_xmpp_url}"
    echo "Installing XMPP server..."
    mkdir -p "${_xmpp_folder}"
    tar xzf "${_installer_folder}/${_xmpp_file}" -C "${_xmpp_folder}"
}

function installLabwiki() {
    checkEnvironmentForLabwiki
    echo "Cloning Labwiki code..."
    mkdir -p "${_labwiki_folder}"
    cd "${_labwiki_folder}"
    git clone -q ${_labwiki_git_url}
    echo "Installing Labwiki..."
    cd ${_labwiki_root}
    bundle install --path vendor
    bundle exec rake post-install
    ${_labwiki_root}/install_plugin https://github.com/mytestbed/labwiki_experiment_plugin.git
    echo "Installation finished."
    echo "Save to ~/.bashrc: export LABWIKI_TOP=${_labwiki_root}"
}

function installRuby() {
    removeOldRuby
    echo "Installing ruby 1.9.3-p286 via rvm..."
    apt-get install -qq -y build-essential libxml2-dev libxslt-dev libssl-dev
    \curl -L https://get.rvm.io | bash -s stable
    source /etc/profile.d/rvm.sh
    rvm --quiet-curl install ruby-1.9.3-p286 --autolibs=4
    rvm use ruby-1.9.3-p286 --default
    gem install bundler
    gem install rake
    rvm current; ruby -v
    echo "Installation finished."
    echo "Save to ~/.bashrc: source /etc/profile.d/rvm.sh"
    echo "To use ruby, you need to add your user to the 'rvm' group: sudo adduser <username> rvm"
    echo "Finally, logout and login again."
}

function configXMPP() {
    echo "Configuring XMPP server..."
    curl -fsSSkL -o "${_installer_folder}/${_xmpp_config}" "${_xmpp_config_url}"
    curl -fsSSkL -o "${_installer_folder}/${_xmpp_db_props}" "${_xmpp_db_props_url}"
    curl -fsSSkL -o "${_installer_folder}/${_xmpp_db_values}" "${_xmpp_db_values_url}"
    curl -fsSSkL -o "${_installer_folder}/${_xmpp_keystore}" "${_xmpp_keystore_url}"
    mkdir -p "${_xmpp_root}/${_xmpp_config_path}"
    cp "${_installer_folder}/${_xmpp_config}" "${_xmpp_root}/${_xmpp_config_path}"
    mkdir -p "${_xmpp_root}/${_xmpp_db_props_path}"
    cp "${_installer_folder}/${_xmpp_db_props}" "${_xmpp_root}/${_xmpp_db_props_path}"
    mkdir -p "${_xmpp_root}/${_xmpp_db_values_path}"
    cp "${_installer_folder}/${_xmpp_db_values}" "${_xmpp_root}/${_xmpp_db_values_path}"
    mkdir -p "${_xmpp_root}/${_xmpp_keystore_path}"
    cp "${_installer_folder}/${_xmpp_keystore}" "${_xmpp_root}/${_xmpp_keystore_path}"
}

function installContainer() {
    echo "Downloading container..."
    mkdir -p "${_installer_folder}"
    curl -C - -fsSSkL -o "${_installer_folder}/${_container_file}" "${_container_url}"
    echo "Installing container..."
    mkdir -p "${_installer_folder}"
    mkdir -p "${_container_folder}"
    unzip -qo "${_installer_folder}/${_container_file}" -d "${_container_folder}"
    rm -r "${_container_root}" 2>/dev/null
    mv "${_container_folder}/${_container_name}" "${_container_root}"
}

function configContainer() {
  configContainerFromCheckout
}

function configContainerFromMaster() {
    echo "Configuring container from github..."

    curl -fsSSkL -o "${_installer_folder}/${_container_config}" "${_container_config_url}"
    cp "${_installer_folder}/${_container_config}" "${_container_root}/standalone/configuration"

    curl -fsSSkL -o "${_installer_folder}/${_container_keystore}" "${_container_keystore_url}"
    cp "${_installer_folder}/${_container_keystore}" "${_container_root}/standalone/configuration"
    curl -fsSSkL -o "${_installer_folder}/${_container_truststore}" "${_container_truststore_url}"
    cp "${_installer_folder}/${_container_truststore}" "${_container_root}/standalone/configuration"
    (
      cd "${_container_root}"
      ./bin/add-user.sh -s -u "${_wildfly_admin_user}" -p "${_wildfly_admin_pwd}"
      ./bin/add-user.sh -s -a -g "${_wildfly_app_group}" -u "${_wildfly_app_user}" -p "${_wildfly_app_pwd}"
    )
    curl -fsSSkL -o "${_installer_folder}/${_container_index}" "${_container_index_url}"
    cp "${_installer_folder}/${_container_index}" "${_container_root}/welcome-content/"
    curl -fsSSkL -o "${_installer_folder}/${_container_css}" "${_container_css_url}"
    cp "${_installer_folder}/${_container_css}" "${_container_root}/welcome-content/"
    curl -fsSSkL -o "${_installer_folder}/${_container_bg}" "${_container_bg_url}"
    cp "${_installer_folder}/${_container_bg}" "${_container_root}/welcome-content/"
    curl -fsSSkL -o "${_installer_folder}/${_container_logo}" "${_container_logo_url}"
    cp "${_installer_folder}/${_container_logo}" "${_container_root}/welcome-content/"
    curl -fsSSkL -o "${_installer_folder}/${_container_jQuery}" "${_container_jQuery_url}"
    mkdir -p "${_container_root}/welcome-content/js/"
    cp "${_installer_folder}/${_container_jQuery}" "${_container_root}/welcome-content/js/"
    cp "${_installer_folder}/${_container_mimic}" "${_container_root}/welcome-content/js/"
}

function configContainerFromCheckout() {
    echo "Configuring container from checkout..."
    [ ! -d "${_base}/bootstrap" ] && echo "bootstrap not found" && exit 1
    _wildfly_res_path="${_base}/bootstrap/resources/wildfly"

    [ ! -z "${WILDFLY_HOME}" ] || WILDFLY_HOME="${_container_root}"
		WILDFLY_HOME="${_base}/server/wildfly"
    cp "${_wildfly_res_path}/standalone/configuration/${_container_keystore}" "${_container_root}/standalone/configuration"
    cp "${_wildfly_res_path}/standalone/configuration/${_container_truststore}" "${_container_root}/standalone/configuration"

    #
    runcmd mkdir -p "${HOME}/.fiteagle"
    runcmd cp "${_wildfly_res_path}/standalone/configuration/${_container_keystore}" "${HOME}/.fiteagle"
    runcmd cp "${_wildfly_res_path}/standalone/configuration/${_container_truststore}" "${HOME}/.fiteagle"

    CMD="${_container_root}/bin/jboss-cli.sh"
    ${CMD} --file="${_wildfly_res_path}/standalone/configuration/${_container_init_config}"
    (
      cd "${_container_root}"
      ./bin/add-user.sh -s -u "${_wildfly_admin_user}" -p "${_wildfly_admin_pwd}"
      ./bin/add-user.sh -s -a -g "${_wildfly_app_group}" -u "${_wildfly_app_user}" -p "${_wildfly_app_pwd}"
    )
    cp "${_wildfly_res_path}/welcome-content/${_container_index}" "${_container_root}/welcome-content/"
    cp "${_wildfly_res_path}/welcome-content/${_container_css}" "${_container_root}/welcome-content/"
    cp "${_wildfly_res_path}/welcome-content/${_container_bg}" "${_container_root}/welcome-content/"
    cp "${_wildfly_res_path}/welcome-content/${_container_logo}" "${_container_root}/welcome-content/"
    mkdir -p "${_container_root}/welcome-content/js/"
    cp "${_wildfly_res_path}/welcome-content/${_container_jQuery}" "${_container_root}/welcome-content/js/"
    cp "${_wildfly_res_path}/welcome-content/${_container_mimic}" "${_container_root}/welcome-content/js/"
}

function checkEnvironmentMinimal {
  _error=0
  echo "Checking environment..."
  checkBinary java; _error=$(($_error + $?))
  checkBinary git; _error=$(($_error + $?))
  checkBinary curl; _error=$(($_error + $?))
  checkBinary unzip; _error=$(($_error + $?))
	checkDirectory JAVA_HOME ${JAVA_HOME}; _error=$(($_error + $?))
  if [ "0" != "$_error" ]; then
    echo >&2 "FAILED. Please install the above mentioned binaries."
    exit 1
  fi
}

function checkEnvironmentDev {
  _error=0
  echo "Checking environment..."
  checkBinary javac; _error=$(($_error + $?))
  checkBinary mvn; _error=$(($_error + $?))
  checkBinary git; _error=$(($_error + $?))
  #checkBinary screen; _error=$(($_error + $?))
  checkBinary svn; _error=$(($_error + $?))
  checkDirectory JAVA_HOME ${JAVA_HOME}; _error=$(($_error + $?))
  if [ "0" != "$_error" ]; then
    echo >&2 "FAILED. Please install the above mentioned binaries."
    exit 1
  fi
}

function checkEnvironment {
  checkEnvironmentMinimal
  checkEnvironmentDev
}

function checkEnvironmentForLabwiki {
  _error=0
  echo "Checking environment for Labwiki..."
  checkPackage libpq-dev; _error=$(($_error + $?))
  checkPackage libicu-dev; _error=$(($_error + $?))
  checkBinary ruby; _error=$(($_error + $?))
  checkRubyVersion; _error=$(($_error + $?))
  if [ "0" != "$_error" ]; then
    echo >&2 "FAILED. Please install the above mentioned binaries."
    exit 1
  fi
}

function removeOldRuby {
  echo "Removing old ruby versions..."
  apt-get -qq -y --purge remove ruby ruby1.8 ruby1.8-dev ruby1.9.3 ruby1.9.1 ruby1.9.1-dev
  rm -rf /usr/share/ruby-rvm /etc/rvmrc /etc/profile.d/rvm.sh
  apt-get -qq -y autoremove
}

function installFITeagleModule {
  repo="$1"
  _src_folder="${_base}/${repo}"
  git_url="https://github.com/FITeagle/${repo}.git"

  if [ -d "${_src_folder}/.git" ]; then
    echo -n "Updating FITeagle ${repo} sources..."
    (cd "${_src_folder}" && git pull -q)
  else
    echo -n "Getting FITeagle ${repo} sources..."
    git clone -q --recursive --depth 1 ${git_url} ${_src_folder}
  fi

  echo "OK"
}

function startXMPP() {
    echo "Starting XMPP Server..."
    [ ! -z "${OPENFIRE_HOME}" ] || OPENFIRE_HOME="${_xmpp_root}"
    export OPENFIRE_CMD="${OPENFIRE_HOME}/bin/openfire"
    export OPENFIRE_LOG="${OPENFIRE_HOME}/logs/stdoutt.log"
    [ -f "${OPENFIRE_CMD}" ] || { echo "Please set OPENFIRE_HOME first "; exit 3; }
    ${OPENFIRE_CMD} start
    echo "Check logs at $OPENFIRE_LOG"
}

function stopXMPP() {
    echo "Stopping XMPP Server..."
    [ ! -z "${OPENFIRE_HOME}" ] || OPENFIRE_HOME="${_xmpp_root}"
    export OPENFIRE_CMD="${OPENFIRE_HOME}/bin/openfire"
    [ -f "${OPENFIRE_CMD}" ] || { echo "Please set OPENFIRE_HOME first "; exit 3; }
    ${OPENFIRE_CMD} stop
}

function startContainerScreen() {
    prepareTruststore
    echo "Starting J2EE Container..."
    [ ! -z "${WILDFLY_HOME}" ] || WILDFLY_HOME="${_container_root}"
		WILDFLY_HOME="${_base}/server/wildfly"
    CMD="${_base}/server/wildfly/bin/standalone.sh"
    RDF=" -Dinfo.aduna.platform.appdata.basedir=../sesame"
    [ -x "${CMD}" ] || { echo "Please set WILDFLY_HOME first "; exit 2; }
    cd "${_base}/server/wildfly"
    screen -S wildfly -dm ${CMD}${RDF} -b 0.0.0.0 -c "${_container_config}" -Djava.security.egd=file:/dev/./urandom ${WILDFLY_ARGS}
    echo "Now running in background, to show it run:"
    echo "screen -R wildfly"
}
function startContainer() {
    startContainerService
}

function prepareTruststore() {
    echo "=============== prepareTruststore ================"
    mkdir ${HOME}/.q-team
    if [ ! -f "${HOME}/.q-team/${_container_keystore}" ]; then
      echo "**Preparing Truststore in: \"${HOME}/.q-team/${_container_keystore}\""
      cp "${_container_root}/standalone/configuration/${_container_keystore}" "${HOME}/.q-team"
    else
      echo "**Truststore \"${HOME}/.q-team/${_container_keystore}\" is present"
    fi
    if [ ! -f "${HOME}/.q-team/${_container_truststore}" ]; then
      echo "**Preparing Truststore in: \"${HOME}/.q-team/${_container_truststore}\""
      cp "${_container_root}/standalone/configuration/${_container_truststore}" "${HOME}/.q-team"
    else
      echo "**Truststore \"${HOME}/.q-team/${_container_truststore}\" is present"
    fi

    echo "dir: ${HOME}/.q-team/"
    ls -al "${HOME}/.q-team/"
    echo "------------ prepareTruststore ------------"
}

function startContainerService() {
    prepareTruststore
    echo "Starting J2EE Container as service..."
    [ ! -z "${WILDFLY_HOME}" ] || WILDFLY_HOME="${_container_root}"
    CMD="${WILDFLY_HOME}/bin/standalone.sh"
    RDF=" -Dinfo.aduna.platform.appdata.basedir=../sesame"
    [ -x "${CMD}" ] || { echo "Please set WILDFLY_HOME first "; exit 2; }
    cd "${WILDFLY_HOME}"
    _LOGFILE="/dev/null"
    echo "logging to ${_LOGFILE}"
    LAUNCH_JBOSS_IN_BACKGROUND=1 ${CMD} ${RDF} --debug 8787 -b 0.0.0.0 -Djava.security.egd=file:/dev/./urandom -c "${_container_config}" ${WILDFLY_ARGS} 2>&1 >$_LOGFILE &
    #java -D[Standalone] -server -Xms64m -Xmx512m -XX:MaxPermSize=256m -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=org.jboss.byteman -Djava.awt.headless=true -agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=n -Dorg.jboss.boot.log.file=/opt/fiteagle/server/wildfly/standalone/log/server.log -Dlogging.configuration=file:/opt/fiteagle/server/wildfly/standalone/configuration/logging.properties -jar /opt/fiteagle/server/wildfly/jboss-modules.jar -mp /opt/fiteagle/server/wildfly/modules org.jboss.as.standalone -Djboss.home.dir=/opt/fiteagle/server/wildfly -Djboss.server.base.dir=/opt/fiteagle/server/wildfly/standalone -Dinfo.aduna.platform.appdata.basedir=../sesame -b 0.0.0.0 -Djava.security.egd=file:/dev/./urandom -c standalone-fiteagle.xml -bmanagement=0.0.0.0 2>&1 >/dev/null &
}

function startContainerDebug() {
    prepareTruststore
    echo "Starting J2EE Container in debug mode (port: 8787)..."
    echo "HOME: $HOME user: $USERNAME"


		WILDFLY_HOME="${_base}/server/wildfly"
    CMD="${_base}/server/wildfly/bin/standalone.sh"
    RDF=" -Dinfo.aduna.platform.appdata.basedir=../sesame"

    cd "${_base}/server/wildfly"
    ${CMD}${RDF} --debug 8787 -b 0.0.0.0 -Djava.security.egd=file:/dev/./urandom -c "${_container_config}" ${WILDFLY_ARGS}
}

function stopContainer() {
    echo "Stopping J2EE Container..."
    [ ! -z "${WILDFLY_HOME}" ] || WILDFLY_HOME="${_container_root}"
			WILDFLY_HOME="${_base}/server/wildfly"
    CMD="${_base}/server/wildfly/bin/jboss-cli.sh"
    [ -x "${CMD}" ] || { echo "Please set WILDFLY_HOME first "; exit 2; }
    ${CMD} --connect command=:shutdown
}

function restartContainer() {
    stopContainer
    startContainer
}

function restartContainerDebug() {
    stopContainer
    startContainerDebug
}

function startLabwiki() {
    echo "Starting Labwiki Server..."
    [ ! -z "${LABWIKI_TOP}" ] || LABWIKI_TOP="${_labwiki_root}"
    CMD="${LABWIKI_TOP}/bin/labwiki"
    [ -x "${CMD}" ] || { echo "Please set LABWIKI_TOP first "; exit 2; }
    cd "${LABWIKI_TOP}"
    ${CMD} --lw-config etc/labwiki/first_test.yaml --lw-no-login start
}

function checkContainer {
    echo "Checking container..."
    isRunning="$(curl -s -m 2 http://localhost:8080 > /dev/null; echo $?)"
    if [ "${isRunning}" != "0" ]; then
      startContainer
    fi
}

function deployOSCO {
    echo "WARNING: this only works within the Fraunhofer FOKUS network. Press ENTER."
    read
    checkContainer
    echo "Getting OSCO..."
    svn checkout "${_osco_url}" "${_base}/osco"

    echo "Building OSCO..."
    cd "${_base}/osco"
    find . -iname "application-*.properties" -exec cp {} "${_container_root}/standalone/configuration" \;
    mvn clean install

    echo "Configuring container..."
    CMD="${_container_root}/bin/jboss-cli.sh"
    ${CMD} --connect command="data-source remove --name=opensdncore"
    ${CMD} --connect command="data-source add --name=opensdncore --connection-url=jdbc:h2:mem:opensdncore;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MVCC=TRUE; --jndi-name=java:jboss/datasources/opensdncore --driver-name=h2 --user-name=neto --password=oten"
    ${CMD} --connect command="jms-topic remove --topic-address=adapterRequestTopic"
    ${CMD} --connect command="jms-topic add --topic-address=adapterRequestTopic --entries=topic/adapterRequestTopic,java:jboss/exported/jms/topic/adapterRequestTopic"
    ${CMD} --connect command="jms-topic remove --topic-address=adapterRequestQueue"
    ${CMD} --connect command="jms-queue add --queue-address=adapterRequestQueue --entries=queue/adapterRequestQueue,java:jboss/exported/jms/queue/adapterRequestQueue"

    echo "Starting OSCO..."
    cd "${_base}/osco"
    mvn wildfly:deploy

    echo "Now open http://localhost:8080/gui"
}

function deployFT1 {
    checkContainer
    installFITeagleModule ft1
    cd "${_base}/ft1" && mvn clean install -DskipTests && mvn wildfly:deploy -DskipTests
}

function deployFT2binary() {
  [ ! -d ".git" ] || { echo "Do not bootstrap within a repository"; exit 4; }

  #(checkBinary git && checkBinary java && checkBinary curl && checkBinary unzip) || (echo "please install missing binaries."; exit 1)
  checkEnvironmentMinimal

  installFITeagleModule bootstrap

  if [ ! -d "${_container_root}" ]; then
    installContainer
    configContainer
  fi
  echo "Downloading binary components from repository..."

  _deployFT2binary_war="org.fiteagle.core:reservation:0.1-SNAPSHOT \
                    org.fiteagle.core:bus:1.0-SNAPSHOT \
                    org.fiteagle.core:orchestrator:0.1-SNAPSHOT \
                    org.fiteagle.core:federationManager:0.1-SNAPSHOT \
                    org.fiteagle.core:resourceAdapterManager:0.1-SNAPSHOT \
                    "
  echo "NOTE: native was removed from default deployment!!"
  echo "      if you need the WEB gui please run \"fiteagle.sh binDeploy-org.fiteagle:native:0.1-SNAPSHOT\""
  echo ""

  if [[ ! -f "${HOME}/.fiteagle/Federation.ttl" ]]; then
    mkdir ${HOME}/.fiteagle
    echo "dowloading defaultFederation.ttl"
    curl -sSL https://github.com/FITeagle/core/raw/master/federationManager/src/main/resources/ontologies/defaultFederation.ttl -o /${HOME}/.fiteagle/Federation.ttl
  fi

  for component in ${_deployFT2binary_war}; do
    deployBin ${component}
  done

  deployBin "org.fiteagle.adapters:sshService:0.1-SNAPSHOT"

  deploySesame

  echo "binary deployment DONE."
}

function deployFT2 {
    checkEnvironmentDev
    checkContainer

    installFITeagleModule api
    cd "${_base}/api" && mvn -DskipTests clean install

    installFITeagleModule core
    cd "${_base}/core" && mvn -DskipTests clean install wildfly:deploy
    if [[ ! -f "${HOME}/.fiteagle/Federation.ttl" ]]; then
      mkdir ${HOME}/.fiteagle
      echo "using defaultFederation.ttl"
      cp "${_base}/core/federationManager/src/main/resources/ontologies/defaultFederation.ttl" "${HOME}/.fiteagle/Federation.ttl"
    fi

    installFITeagleModule native
    cd "${_base}/native" && mvn -DskipTests clean install wildfly:deploy

    installFITeagleModule adapters
    cd "${_base}/adapters/abstract" && mvn -DskipTests clean install
    cd "${_base}/adapters/sshService" && mvn -DskipTests clean install wildfly:deploy
}

function deployFT2sfaBinary {

    installFITeagleModule integration-test

    deployBin "org.fiteagle.north:sfa:0.1-SNAPSHOT"

    if [[ ! -f "${HOME}/.fiteagle/MotorGarage.properties" ]]; then
      mkdir ${HOME}/.fiteagle
      echo "dowloading MotorGarage.properties"
      curl -sSL https://raw.githubusercontent.com/FITeagle/integration-test/master/conf/MotorGarage.properties -o /${HOME}/.fiteagle/MotorGarage.properties
    fi
		if [[ ! -f "${HOME}/.fiteagle/NetworkingAdapter.properties" ]]; then
      mkdir ${HOME}/.fiteagle
      echo "dowloading NetworkingAdapter.properties"
      curl -sSL https://raw.githubusercontent.com/FITeagle/integration-test/master/conf/NetworkingAdapter.properties -o /${HOME}/.fiteagle/NetworkingAdapter.properties
    fi

    deployBin "org.fiteagle.adapters:motor:0.1-SNAPSHOT"
    deployBin "org.fiteagle.adapters:networking:0.1-SNAPSHOT"
}

function deployFT2sfa {
    checkEnvironmentDev
    installFITeagleModule sfa
    cd "${_base}/sfa" && mvn clean wildfly:deploy

		installFITeagleModule integration-test
    if [[ ! -f "${HOME}/.fiteagle/MotorGarage.properties" ]]; then
      mkdir ${HOME}/.fiteagle
      cp "${_base}/integration-test/conf/MotorGarage.properties" "${HOME}/.fiteagle/MotorGarage.properties"
    fi
		if [[ ! -f "${HOME}/.fiteagle/NetworkingAdapter.properties" ]]; then
      mkdir ${HOME}/.fiteagle
      cp "${_base}/integration-test/conf/NetworkingAdapter.properties" "${HOME}/.fiteagle/NetworkingAdapter.properties"
    fi

    installFITeagleModule adapters
    cd "${_base}/adapters/motor" && mvn -DskipTests clean wildfly:deploy
		cd "${_base}/adapters/networking" && mvn -DskipTests clean wildfly:deploy
}

function testFT2sfa {

    if [ -f "${_base}/bootstrap/bin/xmlrpc-client.sh" ]; then
      echo "waiting for server to be ready..."
      CNT=0
      while [[ ! $(${_base}/bootstrap/bin/xmlrpc-client.sh -t https://localhost:8443/sfa/api/am/v3 GetVersion) ]]; do
        echo "sleep 15..."
        sleep 15
        CNT=$((${CNT}+1))
        if [ ${CNT} -gt "20" ]; then
          echo "cnt:" ${CNT}
          echo timeout !
          #screen -S wildfly -X kill
          exit 1
        fi
      done
    fi

    [ -z ${WILDFLY_HOME} ] && export WILDFLY_HOME="${_base}/server/wildfly"

    if [ -d "${_base}/integration-test" ]; then
      cd "${_base}/integration-test" && ./runJfed_local.sh
    elif [ -d "${_base}/sfa" ]; then
      cd "${_base}/sfa" && ./src/test/bin/runJfed.sh
    else
      echo "\"integration-test\" or \"sfa\" sources are missing !! can't run test"
      return 23
    fi
}

function createAdapter() {
		folder="${_base}/../adapters"
		script="createNewAdapter.sh"

		if [ ! -f "${folder}/${script}" ]; then
			return 33;
		fi

		echo -n "Adapter name (e.g. 'CoffeeMachine'): "
		read adapterName
		echo -n "Resource name (e.g. 'Coffee'): "
		read resourceName

		cd ${folder} || exit 34
		bash ${script} ${adapterName} ${resourceName}

}
function bootstrap() {
    [ ! -d ".git" ] || { echo "Do not bootstrap within a repository"; exit 4; }
    checkEnvironmentMinimal

    #installFITeagleModule bootstrap

    #installXMPP
    #configXMPP

    installContainer
    configContainer

    deploySesame

    echo "Save to ~/.bashrc: export WILDFLY_HOME=${_container_root}"
    echo "Save to ~/.bashrc: export OPENFIRE_HOME=${_xmpp_root}"
    echo ""
    echo "Now play around with ./bootstrap/fiteagle.sh"
    #./bootstrap/fiteagle.sh
    usage
}

function usage() {
  echo "Usage: $(basename $0) <command> [<command2> ...]";
  echo "  init               - Download and configure all required binaries";
  echo "  sleep-<seconds>    - wait n seconds before running next command (e.g. sleep-10)";
  echo "  startJ2EE          - Start the J2EE service (WildFly)";
  echo "  startJ2EEDebug     - Start the J2EE service with enabled debug port";
  echo "  restartJ2EEDebug   - Restart the J2EE service with enabled debug port";
  echo "  deployFT1          - Deploy FITeagle 1";
  echo "  deployFT2          - Deploy FITeagle 2 (core modules)";
  echo "  deployFT2sfa       - Deploy FITeagle 2 SFA module and core adapters";
  echo "  deployFT2binary    - Deploy FITeagle 2 (core modules) (without maven)";
  echo "  deployFT2sfaBinary - Deploy FITeagle 2 SFA module and core adapters (without maven)";
  echo "  binDeploy-<artefactID> - Deploy Artefact ID from sonatype nexus (e.g.: \"deployBin-org.fiteagle.adapters:tosca:0.1-SNAPSHOT\")";
  echo "  testFT2sfa         - Test FITeagle 2 SFA module and core adapters";
	echo "  showLog            - Show the log file";
	echo "  runJfed            - Downloads and starts the jFed Experimenter GUI";
	echo "  createAdapter      - Create a new adapter"
  echo "  deployOSCO         - Deploy OpenSDNCore Orchestrator";
  echo "  stopJ2EE           - Stop the J2EE service";
  echo "  restartJ2EE        - Restart the J2EE service";
  echo "  startXMPP          - Start the XMPP service (needed e.g. for the IEEE Intercloud";
  echo "  stopXMPP           - Stop the XMPP Service";
  echo "  installLabwiki     - Install LabWiki (OMF client and GUI)";
  echo "  startLabwiki       - Start LabWiki";
  echo "  installRuby        - Install ruby";
  echo "  deploySesame       - Install and configure OpenRDF/Sesame";
  echo "  deployBinaryOnly   - Deploy binary only version of FT2 and WildFly"
  echo "  buildDemoDocker    - Rebuild the Docker images used for demo.fiteagle.org (image name: ft2)";
  echo "  buildDevDocker     - Rebuild Development Docker from current working copy -test"
}

[ "${#}" -eq 0 ] && usage

RESULT=0
[ ! -z "${WILDFLY_HOME}" ] || export WILDFLY_HOME="${_container_root}"

## hotfix for old docker versions
if [[ ${HOME} = "/" ]]; then
  case $(id -nu) in
    root)
      export HOME=/root
    ;;
    *)
      export HOME=/home/$(id -nu)
    ;;
  esac
fi

for arg in "$@"; do
  fold_begin $arg "Running: $arg"
  case $arg in
    bootstrap)
      bootstrap
      RESULT=$(($RESULT+$?))
    ;;
    init)
      bootstrap
      RESULT=$(($RESULT+$?))
    ;;
    sleep-*)
      SEC=$(echo $arg | cut -d'-' -f2)
      echo "sleep ${SEC}"
      sleep ${SEC} || exit 1
    ;;
    startXMPP)
      startXMPP
      RESULT=$(($RESULT+$?))
      ;;
    stopXMPP)
      RESULT=$(($RESULT+$?))
      ;;
    startSPARQL)
      startSPARQL
      RESULT=$(($RESULT+$?))
      ;;
    startJ2EE)
      startContainer
      RESULT=$(($RESULT+$?))
      ;;
    startJ2EEDebug)
      startContainerDebug
      RESULT=$(($RESULT+$?))
      ;;
    restartJ2EEDebug)
      restartContainerDebug
      RESULT=$(($RESULT+$?))
      ;;
    stopJ2EE)
      stopContainer
      RESULT=$(($RESULT+$?))
      ;;
    restartJ2EE)
      restartContainer
      RESULT=$(($RESULT+$?))
      ;;
    deployBin-*)
      ARTEFACT=$(echo $arg | sed 's/binDeploy-//g')
      deployBin $ARTEFACT
      RESULT=$(($RESULT+$?))
    ;;
    binDeploy-*)
      ARTEFACT=$(echo $arg | sed 's/binDeploy-//g')
      deployBin $ARTEFACT
      RESULT=$(($RESULT+$?))
    ;;
    deployFT2)
      deployFT2
      RESULT=$(($RESULT+$?))
      ;;
    deployFT2sfa)
      deployFT2sfa
      RESULT=$(($RESULT+$?))
      ;;
    deployFT2sfaBinary)
      deployFT2sfaBinary
      RESULT=$(($RESULT+$?))
      ;;
    deployFT2binary)
      deployFT2binary
      RESULT=$(($RESULT+$?))
      ;;
    testFT2sfa)
      testFT2sfa
      RESULT=$(($RESULT+$?))
      ;;
    deployFT1)
      deployFT1
      RESULT=$(($RESULT+$?))
      ;;
		showLog)
			less "${_base}/server/wildfly/standalone/log/server.log"
			RESULT=$(($RESULT+$?))
			;;
		runJfed)
			${_dir}/bin/runJfedExperimenterGUI.sh
			RESULT=$(($RESULT+$?))
			;;
		createAdapter)
				createAdapter
				RESULT=$(($RESULT+$?))
				;;
    deployOSCO)
      deployOSCO
      RESULT=$(($RESULT+$?))
      ;;
    installLabwiki)
      installLabwiki
      RESULT=$(($RESULT+$?))
      ;;
    installRuby)
      installRuby
      RESULT=$(($RESULT+$?))
      ;;
    startLabwiki)
      startLabwiki
      RESULT=$(($RESULT+$?))
      ;;
    deploySesame)
      deploySesame
      RESULT=$(($RESULT+$?))
      ;;
    deployBinaryOnly)
      deployBinaryOnly
      RESULT=$(($RESULT+$?))
      ;;
    deployExtraBinary)
      deployExtraBinary
      RESULT=$(($RESULT+$?))
      ;;
    buildDemoDocker)
      buildDemoDocker
      RESULT=$(($RESULT+$?))
      ;;
    buildDevDocker)
      buildDevDocker
      RESULT=$(($RESULT+$?))
      ;;
    bash)
      bash
      ;;
    *)
      echo "Unknown command $arg"
      usage
      fold_end $arg
      exit 1
    ;;
  esac
  echo "RESULT: $RESULT"
  fold_end $arg
done

exit $RESULT
