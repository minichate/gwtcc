package "python-setuptools"
package "python-virtualenv"

include_recipe "apt"
include_recipe "python"

user "app" do
  home "/home/app"
  shell "/bin/bash"
  action :create
end

application "gwtcc" do
  path "/home/app/src"
  owner "app"
  group "app"
  repository "https://github.com/minichate/gwtcc.git"
  revision "master"
  migrate false
  
  django do
  end
end
