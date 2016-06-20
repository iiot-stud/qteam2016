#!/usr/bin/env python
#
#    NEPI, a framework to manage network experiments
#    Copyright (C) 2013 INRIA
#
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
# Author: Alina Quereilhac <alina.quereilhac@inria.fr>
# Author: Alexander Willner <alexander.willner@tu-berlin.de>

from nepi.execution.ec import ExperimentController 
import getpass

hostname = "127.0.0.1"
username = getpass.getuser()
ssh_key = "~/.ssh/id_rsa.pub"

ec = ExperimentController(exp_id = "ping-exp")

node = ec.register_resource("LinuxNode")
ec.set(node, "hostname", hostname)
ec.set(node, "username", username)
ec.set(node, "cleanHome", True)
ec.set(node, "cleanProcesses", True)

app = ec.register_resource("LinuxApplication")
ec.set(app, "command", "ping -c3 8.8.8.8")
ec.register_connection(app, node)

ec.deploy()

ec.wait_finished(app)

print ec.trace(app, "stdout")

ec.shutdown()
