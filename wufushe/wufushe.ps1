
function getLocalImgHashs ($repoPath, $imgHashSet) {
    "Start to get local img file hash from ${repoPath}..."

    $imgHashSet.Clear()

    Get-ChildItem "$repoPath" | ForEach-Object {
      $ret = $imgHashSet.Add((Get-FileHash ("$repoPath" + $_.Name)).Hash)
      if("X${ret}" -eq "X$false") {
        'File ' + $_.Name + ' is duplicated, removing...'
        Remove-Item ("${repoPath}" + $_.Name)
      }
    }
}

function getImg ($ie, $domain, $urlSet, $repoPath, $imgHashSet) {
    if($true -eq $urlSet.Contains($domain) -or '' -eq "$domain") { return }

    $ie.Navigate("$domain")

    For($i = 0; $i -lt 1024; $i++) {
        if($ie.ReadyState -gt 3) { break }
        Start-Sleep -Milliseconds 100
    }

    if ($ie.ReadyState -lt 3) { return }

    $domain

    $imgUrls = $ie.Document.body.getElementsByTagName('img')

    $imgNames = $ie.Document.body.getElementsByTagName('h3')

    if($imgUrls.Length -gt $imgNames.Length) { 
        'The img count does not fit the name count in page: ' + $domain
        return
    }

    For($i = 0; $i -lt $imgNames.Length; $i++) {
        $imgName = (Get-Date -UFormat '%y%m%d%H%M%S') + '-' + ($imgNames[$i].OuterText -replace "[\[\]–&★\\\/\?<>\!]",'') + '.' + ($imgUrls[$i].src -split '\.')[-1]

        "${repoPath}${imgName}"

        $httpResponse = Invoke-WebRequest -PassThru -Uri $imgUrls[$i].src -OutFile  "${repoPath}${imgName}"
        if($httpResponse.StatusCode -lt 300) {
            $fileHash = Get-FileHash "${repoPath}${imgName}"
            if($imgHashSet.Contains($fileHash.Hash)) {
                "${repoPath}${imgName} was already been downloaded."
                Remove-Item "${repoPath}${imgName}"
            } else {
                $imgHashSet.Add($fileHash.Hash)
            }
        } else {
            'Failed to download img: ' + $imgUrls[$i].src
        }
    }

    $urlSet.Add($domain)

    $ie.Document.body.getElementsByTagName('a') | Where-Object { $_.href -match ".*page/.*" } | ForEach-Object {
        getImg $ie $_.href $urlSet $repoPath $imgHashSet
    }
}


function main () {
    # $domain = 'https://wufushe.vip/'
    $domain = 'https://wuyecheng.org/'

    $repoPath = 'E:\wufushe\'

    $imgHashSet = New-Object System.Collections.Generic.HashSet[String]

    getLocalImgHashs $repoPath $imgHashSet

    $ie = New-Object -ComObject InternetExplorer.Application

    $ie.Visible = $false

    $urlSet = New-Object System.Collections.Generic.HashSet[String]

    getImg $ie $domain $urlSet $repoPath $imgHashSet

}



main