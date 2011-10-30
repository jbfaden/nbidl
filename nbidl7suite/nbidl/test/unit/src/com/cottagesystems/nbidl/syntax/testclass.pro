function papco_idlneturl_filesystem::init, url_in
   k= papco_get_keychain()
   url= papco_idlneturl_filesystem_checkUrl( url_in )
   if ( self->papco_filesystem::init( 'wfs '+k->hideCredentials(url) ) ) eq 0 then return,0
   if ( papco_get_default( 'wget' ) eq 0 ) then begin
       self.connect_status= 'network disabled (property wget disabled)'
       self.connected= -1
   endif
   self.rooturl= url

   self.checked_lslrt= 0

   return, 1
end

function papco_idlneturl_filesystem::get_connection_status_code
  return, self.connected
end

pro papco_idlneturl_filesystem__define
   struct= { papco_idlneturl_filesystem, $
             rootUrl:'', $
             checked_lslrt:0, $
             idlneturl:obj_new(), $
             busy:0, $ ;; 1=busy in transaction, 0=idle
             monitor:obj_new(), $
             connected:0, $   ;; =1 connected, =0 undetermined, =-1 offline, must be reset
             connect_status:'', $ ;; human message relating status
             cache: ptr_new(), $
             cacheCompleted:1, $
             inherits papco_filesystem }
end





