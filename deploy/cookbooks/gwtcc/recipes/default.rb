package "python-setuptools"
package "python-virtualenv"

include_recipe "apt"
include_recipe "python"

user "app" do
  home "/home/app"
  shell "/bin/bash"
  action :create
end