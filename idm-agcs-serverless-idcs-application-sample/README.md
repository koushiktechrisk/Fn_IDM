## Pre-requirements

1. Maven 3.6.0, or higher
2. JDK 11
3. Latest container runtime docker/podman
4. Oracle OCI Subscription with access to manage Oracle Functions

## Deploy The Functions
1. Follow the [OCI Guide](https://docs.oracle.com/en-us/iaas/Content/Functions/Tasks/functionsquickstartlocalhost.htm#functionsquickstartlocalhost_topic_setup_your_tenancy) to setup your tenancy for function deployment.
2. [Create an application](https://docs.oracle.com/en-us/iaas/Content/Functions/Tasks/functionsquickstartlocalhost.htm#functionsquickstartlocalhost_topic_setup_Create_application).
3.  [Setup your local environment](https://docs.oracle.com/en-us/iaas/Content/Functions/Tasks/functionsquickstartlocalhost.htm#functionsquickstartlocalhost_topic_start_setting_up_local_dev_environment) or [Setup your OCI Cloud Shell environment](https://docs.oracle.com/en-us/iaas/Content/Functions/Tasks/functionsquickstartcloudshell.htm#functionsquickstartcloudshell_topic_setup_CloudShell_dev_env).
4. Create an application in OCI. Replace with your subnetId.
```shell
fn create app agcs-generic-rest-connector --annotation oracle.com/oci/subnetIds='["<subnetId>"]'
```
5. Populate the config yaml files each functions accordingly.
```text
For IDCS application, populate
* grc-request-template/src/main/resources/request/idcs/config.yaml
* grc-response-template/src/main/resources/response/idcs/config.yaml
* grc-schema-template/src/main/resources/schema/idcs/config.yaml
* grc-test-template/src/main/resources/test/idcs/config.yaml
```
6. Populate `config.properties`, `config` and `oci_api_ke.pem` inside `src/test/resources` folder of each function if you need to run the test cases. `config` file contains connectivity details with OCI service. Refer the [OCI Guide](https://docs.oracle.com/en-us/iaas/Content/API/Concepts/sdkconfig.htm) for more info.
7. Populate the `config.properties` inside `grc-test-infra/src/test/resources` folder to run tests which validates the request template configured in grc-request-template function by actually hitting application target API and map their response to corresponding response template configured in grc-response-template function.
8. Navigate to `idm-agcs-serverless-idcs-application-sample` directory 
```shell
cd idm-agcs-serverless-idcs-application-sample
```
9. Compile and package the functions along with running the testcases.
```shell
mvn clean package -DisDevMode=true
```
or Compile and package the functions without running the testcases.
```shell
mvn clean package -DskipTests=true
```
10. Navigate to the function which you want to deploy.
```shell
cd grc-schema-template
```
11. Deploy the function to OCI
```shell
fn -v deploy --app agcs-generic-rest-connector
```
12. Verify if the function is deployed either from [OCI console](https://docs.oracle.com/en-us/iaas/Content/Functions/Tasks/functionsviewingfunctionsapps.htm#top) or by executing the following command
```shell
fn ls functions agcs-generic-rest-connector | awk {'print Function, $1 "\t\t" $3'}
```


