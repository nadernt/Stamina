/*
Created By:
http://www.html5andbeyond.com
*/

$(document).ready(function(){

$('nav').prepend('<div id="responsive-nav" style="display:none">Menu</div>');
$('#responsive-nav').on('click',function(){
$('nav ul').slideToggle()
});

$(window).resize(function(){

if ($(window).innerWidth() < 768) {
$('nav ul li').css('display','block');
$('nav ul').hide()
$('#responsive-nav').show()
} else {
$('nav ul li').css('display','inline-block');
$('nav ul').show()
$('#responsive-nav').hide()
}

});

$(window).resize();

});

$(document).on('scroll',function(){

if ($(document).scrollTop() > 100) {
$('#nav').addClass('fixed')
} else {
$('#nav').removeClass('fixed')
}

});