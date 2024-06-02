# base64Decode

function base64Decode {
    param(
        [Parameter(Mandatory = $true, Position = 0)]
        [string]$base64String
    )

    $bytes = [System.Convert]::FromBase64String($base64String)
    $decodedString = [System.Text.Encoding]::UTF8.GetString($bytes)
    return $decodedString
}

function base64Encode {
    param (
        [Parameter(Mandatory = $true, Position = 0)]
        [string]$string
    )

    $bytes = [System.Text.Encoding]::UTF8.GetBytes($string)
    $encodedString = [System.Convert]::ToBase64String($bytes)
    return $encodedString
}


