$imgNameSet = New-Object System.Collections.Generic.HashSet[String]

$imgHashSet = New-Object System.Collections.Generic.HashSet[String]

function getLocalImgHashs() {
  'Start to get local img file hash...'
  Get-ChildItem "$repoPath" | ForEach-Object {
    $ret = $imgHashSet.Add((Get-FileHash ("$repoPath" + $_.Name)).Hash)
    if("X${ret}" -eq "X$false") {
      'File ' + $_.Name + ' is duplicated, removing...'
      Remove-Item ("${repoPath}" + $_.Name)
    }
  }
}

function getImg ($ie, $targetUrl, $repoPath) {
  $ie.Navigate($targetUrl)

  For($i = 0; $i -lt 1024; $i++) {
    if($ie.ReadyState -gt 3) { break }
    Start-Sleep -Milliseconds 100
  }

  if ($ie.ReadyState -lt 3) { return }

  ## $imgUrls = $ie.Document.body.getElementsByTagName('img') | Where-Object { ($_.src -match ".*sina.*") -or ($_.src -match ".*weibo.*") }

  $imgUrls = $ie.Document.body.getElementsByTagName('img') | Where-Object { ($_.src -match ".*sina.*\.gif$") -or ($_.src -match ".*weibo.*\.gif$") }

  if("X$False" -eq "X$?") { return }

  'Current page img count: ' + $imgUrls.Count
  
  $imgUrls | ForEach-Object {
    if($_.src.Length -gt 0) {
      $imgName = ($_.src -split '/')[-1]

      if($true -ne $imgNameSet.Contains($imgName)) {
        "img src = " + $_.src
        $tempFilePath = "${repoPath}" + (Get-Date -UFormat '%y%m%d%H%M%S') + "-${imgName}"
        $httpResponse = Invoke-WebRequest -PassThru -Uri $_.src -OutFile "$tempFilePath" -TimeoutSec 24
        if(("X$?" -eq "XTrue") -and ($httpResponse.StatusCode -lt 300)) {
          $fileHash = Get-FileHash "${tempFilePath}"
          if("X$?" -eq "XTrue") {
            if($true -eq $imgHashSet.Contains($fileHash.Hash)) {
              "$tempFilePath was already been downloaded."
              Remove-Item "${tempFilePath}"
            }
          }
          $imgNameSet.Add($imgName)
        }
      }
    }
  }

  Start-Sleep -Milliseconds 500

}

function getJiandanOOXX ($ie) {
  $repoPath = 'E:\jiandan-ooxx\'

  getLocalImgHashs
  
  For($i = 1024; $i -gt 0; $i--) {
    $refix = ($i % 45) + 1
    $targetUrl = 'http://jandan.net/ooxx/page-' + $refix
  
    $targetUrl

    getImg $ie $targetUrl $repoPath
  }

}

function getJiandanPic($ie) {
  $repoPath = 'E:\jiandan-pic\'

  getLocalImgHashs
  for($i = 1024; $i -gt 0; $i--) {
    $refix = ($i % 400) + 1
    $targetUrl = 'http://jandan.net/pic/page-' + $refix

    $targetUrl

    getImg $ie $targetUrl $repoPath
  }
  
}

function main () {
  $ie = New-Object -ComObject InternetExplorer.Application
  
  $ie.Visible = $false

  getJiandanPic $ie
}

main







