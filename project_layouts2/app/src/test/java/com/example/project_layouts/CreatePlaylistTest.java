package com.example.project_layouts;

import org.junit.Test;

import static org.junit.Assert.*;

public class CreatePlaylistTest {

    CreatePlaylist createPlaylist = new CreatePlaylist();


    @Test
    public void correctDetails() {
        boolean result = createPlaylist.checkPlaylistDetails( "rom");
        assertEquals(true, result);
    }

    @Test
    public void EmptyPlaylistName() {
        boolean result = createPlaylist.checkPlaylistDetails( "");
        assertEquals(false, result);
    }

}