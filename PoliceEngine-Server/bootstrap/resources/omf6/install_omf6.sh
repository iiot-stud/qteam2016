#!/bin/bash

function removeOldRuby {
 apt-get -y --purge remove ruby-rvm ruby ruby1.9.3
 rm -rf /usr/share/ruby-rvm /etc/rvmrc /etc/profile.d/rvm.sh
 apt-get -y autoremove
}

function installNeededLibs {
 apt-get install -y build-essential libxml2-dev libxslt-dev libssl-dev
}

function installRuby {
 \curl -L https://get.rvm.io | bash -s stable
 source /etc/profile.d/rvm.sh
 rvm install ruby-1.9.3-p286 --autolibs=4
 rvm use ruby-1.9.3-p286
 rvm gemset create omf
 rvm use ruby-1.9.3-p286@omf --default

 rvm current; ruby -v
}

function installOmfRc {
 gem install -V omf_rc --no-ri --no-rdoc
 install_omf_rc -i -c
 sed -i  "s/localhost/fuseco.fokus.fraunhofer.de/g" /etc/omf_rc/config.yml
}

function startOmfRc {
 start omf_rc
}

removeOldRuby
installNeededLibs
installRuby
installOmfRc
startOmfRc

