//live validate for blank input
function liveValidateBlank(arg){
    var data = document.getElementById(arg).value;
    if(data.length==0)
        document.getElementById(arg).style.border = "1px solid red";
    else
        document.getElementById(arg).style.border = "1px solid green";
}
//live validation for strings
function liveValidateStr(arg){
    var dat = document.getElementById(arg).value;
    var reg=/^[A-Za-z][A-Za-z\s]*$/;
    if(reg.test(dat)==true)
        document.getElementById(arg).style.border = "1px solid green";
    else
        document.getElementById(arg).style.border = "1px solid red";
}