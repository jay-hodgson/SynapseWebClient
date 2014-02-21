var COMPILED=!0,goog=goog||{};goog.global=this;goog.DEBUG=!0;goog.LOCALE="en";goog.evalWorksForGlobals_=null;goog.provide=function(a){if(!COMPILED){if(goog.getObjectByName(a)&&!goog.implicitNamespaces_[a])throw Error('Namespace "'+a+'" already declared.');for(var b=a;b=b.substring(0,b.lastIndexOf("."));)goog.implicitNamespaces_[b]=!0}goog.exportPath_(a)};COMPILED||(goog.implicitNamespaces_={});
goog.exportPath_=function(a,b,c){a=a.split(".");c=c||goog.global;a[0]in c||!c.execScript||c.execScript("var "+a[0]);for(var d;a.length&&(d=a.shift());)!a.length&&goog.isDef(b)?c[d]=b:c=c[d]?c[d]:c[d]={}};goog.getObjectByName=function(a,b){for(var c=a.split("."),d=b||goog.global,e;e=c.shift();)if(d[e])d=d[e];else return null;return d};goog.globalize=function(a,b){var c=b||goog.global,d;for(d in a)c[d]=a[d]};
goog.addDependency=function(a,b,c){if(!COMPILED){var d;a=a.replace(/\\/g,"/");for(var e=goog.dependencies_,g=0;d=b[g];g++)e.nameToPath[d]=a,a in e.pathToNames||(e.pathToNames[a]={}),e.pathToNames[a][d]=!0;for(d=0;b=c[d];d++)a in e.requires||(e.requires[a]={}),e.requires[a][b]=!0}};
goog.require=function(a){if(!COMPILED&&!goog.getObjectByName(a)){var b=goog.getPathFromDeps_(a);if(b)goog.included_[b]=!0,goog.writeScripts_();else throw a="goog.require could not find: "+a,goog.global.console&&goog.global.console.error(a),Error(a);}};goog.useStrictRequires=!1;goog.basePath="";goog.nullFunction=function(){};goog.identityFunction=function(a){return a};goog.abstractMethod=function(){throw Error("unimplemented abstract method");};
goog.addSingletonGetter=function(a){a.getInstance=function(){return a.instance_||(a.instance_=new a)}};
COMPILED||(goog.included_={},goog.dependencies_={pathToNames:{},nameToPath:{},requires:{},visited:{},written:{}},goog.inHtmlDocument_=function(){var a=goog.global.document;return"undefined"!=typeof a&&"write"in a},goog.findBasePath_=function(){if(goog.inHtmlDocument_()){var a=goog.global.document;if(goog.global.CLOSURE_BASE_PATH)goog.basePath=goog.global.CLOSURE_BASE_PATH;else for(var a=a.getElementsByTagName("script"),b=a.length-1;0<=b;--b){var c=a[b].src,d=c.length;if("base.js"==c.substr(d-7)){goog.basePath=
c.substr(0,d-7);break}}}},goog.writeScriptTag_=function(a){goog.inHtmlDocument_()&&!goog.dependencies_.written[a]&&(goog.dependencies_.written[a]=!0,goog.global.document.write('<script type="text/javascript" src="'+a+'">\x3c/script>'))},goog.writeScripts_=function(){function a(e){if(!(e in d.written)){if(!(e in d.visited)&&(d.visited[e]=!0,e in d.requires))for(var f in d.requires[e])if(f in d.nameToPath)a(d.nameToPath[f]);else if(!goog.getObjectByName(f))throw Error("Undefined nameToPath for "+f);
e in c||(c[e]=!0,b.push(e))}}var b=[],c={},d=goog.dependencies_,e;for(e in goog.included_)d.written[e]||a(e);for(e=0;e<b.length;e++)if(b[e])goog.writeScriptTag_(goog.basePath+b[e]);else throw Error("Undefined script input");},goog.getPathFromDeps_=function(a){return a in goog.dependencies_.nameToPath?goog.dependencies_.nameToPath[a]:null},goog.findBasePath_(),goog.global.CLOSURE_NO_DEPS||goog.writeScriptTag_(goog.basePath+"deps.js"));
goog.typeOf=function(a){var b=typeof a;if("object"==b)if(a){if(a instanceof Array||!(a instanceof Object)&&"[object Array]"==Object.prototype.toString.call(a)||"number"==typeof a.length&&"undefined"!=typeof a.splice&&"undefined"!=typeof a.propertyIsEnumerable&&!a.propertyIsEnumerable("splice"))return"array";if(!(a instanceof Object||"[object Function]"!=Object.prototype.toString.call(a)&&("undefined"==typeof a.call||"undefined"==typeof a.propertyIsEnumerable||a.propertyIsEnumerable("call"))))return"function"}else return"null";
else if("function"==b&&"undefined"==typeof a.call)return"object";return b};goog.propertyIsEnumerableCustom_=function(a,b){if(b in a)for(var c in a)if(c==b&&Object.prototype.hasOwnProperty.call(a,b))return!0;return!1};goog.propertyIsEnumerable_=function(a,b){return a instanceof Object?Object.prototype.propertyIsEnumerable.call(a,b):goog.propertyIsEnumerableCustom_(a,b)};goog.isDef=function(a){return void 0!==a};goog.isNull=function(a){return null===a};
goog.isDefAndNotNull=function(a){return null!=a};goog.isArray=function(a){return"array"==goog.typeOf(a)};goog.isArrayLike=function(a){var b=goog.typeOf(a);return"array"==b||"object"==b&&"number"==typeof a.length};goog.isDateLike=function(a){return goog.isObject(a)&&"function"==typeof a.getFullYear};goog.isString=function(a){return"string"==typeof a};goog.isBoolean=function(a){return"boolean"==typeof a};goog.isNumber=function(a){return"number"==typeof a};
goog.isFunction=function(a){return"function"==goog.typeOf(a)};goog.isObject=function(a){a=goog.typeOf(a);return"object"==a||"array"==a||"function"==a};goog.getHashCode=function(a){if(a.hasOwnProperty&&a.hasOwnProperty(goog.HASH_CODE_PROPERTY_))return a[goog.HASH_CODE_PROPERTY_];a[goog.HASH_CODE_PROPERTY_]||(a[goog.HASH_CODE_PROPERTY_]=++goog.hashCodeCounter_);return a[goog.HASH_CODE_PROPERTY_]};goog.removeHashCode=function(a){"removeAttribute"in a&&a.removeAttribute(goog.HASH_CODE_PROPERTY_);try{delete a[goog.HASH_CODE_PROPERTY_]}catch(b){}};
goog.HASH_CODE_PROPERTY_="closure_hashCode_"+Math.floor(2147483648*Math.random()).toString(36);goog.hashCodeCounter_=0;goog.cloneObject=function(a){var b=goog.typeOf(a);if("object"==b||"array"==b){if(a.clone)return a.clone.call(a);var b="array"==b?[]:{},c;for(c in a)b[c]=goog.cloneObject(a[c]);return b}return a};
goog.bind=function(a,b,c){var d=b||goog.global;if(2<arguments.length){var e=Array.prototype.slice.call(arguments,2);return function(){var b=Array.prototype.slice.call(arguments);Array.prototype.unshift.apply(b,e);return a.apply(d,b)}}return function(){return a.apply(d,arguments)}};goog.partial=function(a,b){var c=Array.prototype.slice.call(arguments,1);return function(){var b=Array.prototype.slice.call(arguments);b.unshift.apply(b,c);return a.apply(this,b)}};
goog.mixin=function(a,b){for(var c in b)a[c]=b[c]};goog.now=Date.now||function(){return+new Date};
goog.globalEval=function(a){if(goog.global.execScript)goog.global.execScript(a,"JavaScript");else if(goog.global.eval)if(null==goog.evalWorksForGlobals_&&(goog.global.eval("var _et_ = 1;"),"undefined"!=typeof goog.global._et_?(delete goog.global._et_,goog.evalWorksForGlobals_=!0):goog.evalWorksForGlobals_=!1),goog.evalWorksForGlobals_)goog.global.eval(a);else{var b=goog.global.document,c=b.createElement("script");c.type="text/javascript";c.defer=!1;c.appendChild(b.createTextNode(a));b.body.appendChild(c);
b.body.removeChild(c)}else throw Error("goog.globalEval not available");};goog.typedef=!0;goog.getCssName=function(a,b){var c=a+(b?"-"+b:"");return goog.cssNameMapping_&&c in goog.cssNameMapping_?goog.cssNameMapping_[c]:c};goog.setCssNameMapping=function(a){goog.cssNameMapping_=a};goog.getMsg=function(a,b){var c=b||{},d;for(d in c)a=a.replace(RegExp("\\{\\$"+d+"\\}","gi"),c[d]);return a};goog.exportSymbol=function(a,b,c){goog.exportPath_(a,b,c)};goog.exportProperty=function(a,b,c){a[b]=c};
goog.inherits=function(a,b){function c(){}c.prototype=b.prototype;a.superClass_=b.prototype;a.prototype=new c;a.prototype.constructor=a};
goog.base=function(a,b,c){var d=arguments.callee.caller;if(d.superClass_)return d.superClass_.constructor.apply(a,Array.prototype.slice.call(arguments,1));for(var e=Array.prototype.slice.call(arguments,2),g=!1,f=a.constructor;f;f=f.superClass_&&f.superClass_.constructor)if(f.prototype[b]===d)g=!0;else if(g)return f.prototype[b].apply(a,e);if(a[b]===d)return a.constructor.prototype[b].apply(a,e);throw Error("goog.base called from a method of one name to a method of a different name");};
