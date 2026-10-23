param(
    [Parameter(Mandatory = $true)][string]$GlassFishHome,
    [string]$Servidor = 'localhost',
    [int]$Porta = 1433,
    [string]$Usuario = 'loja'
)

$ErrorActionPreference = 'Stop'
if (-not $env:LOJA_PASSWORD) {
    throw 'Defina LOJA_PASSWORD com a senha do usuário do banco antes de executar.'
}
$asadmin = Join-Path $GlassFishHome 'bin/asadmin.bat'
$arquivo = New-TemporaryFile
try {
    $xml = [xml]@'
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE resources PUBLIC "-//GlassFish.org//DTD GlassFish Application Server 3.1 Resource Definitions//EN" "http://glassfish.org/dtds/glassfish-resources_1_5.dtd">
<resources>
    <jdbc-connection-pool name="SQLServerPool" res-type="javax.sql.DataSource"
        datasource-classname="com.microsoft.sqlserver.jdbc.SQLServerDataSource"
        steady-pool-size="2" max-pool-size="10" is-connection-validation-required="true"
        connection-validation-method="table" validation-table-name="dbo.Produto">
        <property name="serverName" value=""/>
        <property name="portNumber" value=""/>
        <property name="databaseName" value="loja"/>
        <property name="user" value=""/>
        <property name="password" value=""/>
        <property name="encrypt" value="true"/>
        <property name="trustServerCertificate" value="true"/>
    </jdbc-connection-pool>
    <jdbc-resource jndi-name="jdbc/loja" pool-name="SQLServerPool"/>
</resources>
'@
    $valores = @{ serverName = $Servidor; portNumber = "$Porta"; user = $Usuario; password = $env:LOJA_PASSWORD }
    foreach ($propriedade in $xml.resources.'jdbc-connection-pool'.property) {
        if ($valores.ContainsKey($propriedade.name)) {
            $propriedade.SetAttribute('value', $valores[$propriedade.name])
        }
    }
    $xml.Save($arquivo.FullName)
    & $asadmin add-resources $arquivo.FullName
    if ($LASTEXITCODE -ne 0) { throw 'Não foi possível criar os recursos JDBC.' }
    & $asadmin ping-connection-pool SQLServerPool
    if ($LASTEXITCODE -ne 0) { throw 'O pool não conseguiu acessar o banco loja.' }
} finally {
    Remove-Item -LiteralPath $arquivo.FullName -Force
}
