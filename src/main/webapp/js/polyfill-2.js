if (!String.prototype.startsWith) {
    String.prototype.startsWith = function(searchString, position){
      position = position || 0;
      return this.substr(position, searchString.length) === searchString;
  };
}
if (typeof Object.assign != 'function') {
	Object.assign = function(target, varArgs) {
 'use strict';
 if (target == null) { // TypeError if undefined or null
   throw new TypeError('Cannot convert undefined or null to object');
 }
 
 var to = Object(target);
 
 for (var index = 1; index < arguments.length; index++) {
   var nextSource = arguments[index];
 
   if (nextSource != null) { // Skip over if undefined or null
	 for (var nextKey in nextSource) {
	   // Avoid bugs when hasOwnProperty is shadowed
	   if (Object.prototype.hasOwnProperty.call(nextSource, nextKey)) {
		 to[nextKey] = nextSource[nextKey];
		 }
		}
	  }
	}
	return to;
   };
  }
/***************************************************
	SWC-4210: detect first time touchstart is detected, and hide all tooltips!
***************************************************/
window.addEventListener('touchstart', function onFirstTouch() {
	jQuery("<style type='text/css'> .tooltip { display: none !important;} </style>").appendTo("head");
	  // stop listening now
	  window.removeEventListener('touchstart', onFirstTouch, false);
	}, false);
