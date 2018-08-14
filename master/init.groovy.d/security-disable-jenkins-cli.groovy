/*
   Disable Jenkins CLI.
 */

import jenkins.model.Jenkins

Jenkins.instance.getDescriptor("jenkins.CLI").get().setEnabled(false)

println 'Jenkins CLI has been disabled.'
