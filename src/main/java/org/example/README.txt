*sender
quand on clique sur submit il y'aura l'appel de actionPreformed=>service va etre cree ?
*receiver
quand on clique sur le bouton il y'aura l'appel de actionPreformed=>receiveScreenShot
receiveScreenShot:dans laquel on appel getScreen() et sendMouseClick pour envoyer les coordonnees du cliquer a l'image reel
sendMouseClick: dans laquel on appel  server.mousePressedEvent(scaleX, scaleY); pour que lserveur recoit les coordonnes envoyes
