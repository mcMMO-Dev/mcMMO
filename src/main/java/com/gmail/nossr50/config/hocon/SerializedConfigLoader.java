package com.gmail.nossr50.config.hocon;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.mcMMO;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.ValueType;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.util.ConfigurationNodeWalker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;

/*
 * This file is part of GriefPrevention, licensed under the MIT License (MIT).
 *
 * Copyright (c) bloodmc
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/*
 * The code here has been modified from its source
 */

/**
 * Handles loading serialized configs with configurate
 * @param <T> the class type of the config
 */
public class SerializedConfigLoader<T> {
    private static final String CONFIG_HEADER = "Configuration files are now in the HOCON file format!\n" +
            "\nHOCON is a lot less strict than YAML, so don't worry about the number of spaces and such!\n" +
            "\nIt is recommended that you use a nice text editor to view and edit these files" +
            "\n On Windows I recommend VS Code (Free by Microsoft) https://code.visualstudio.com/" +
            "\n On Linux I recommend nvim (Free) https://neovim.io/\n" +
            "\nIf you need help with the configuration files, feel free to come ask for support in our discord!" +
            "\nOfficial mcMMO Discord - https://discord.gg/bJ7pFS9\n" +
            "\nYou can also consult the new official wiki" +
            "\nhttps://mcmmo.org/wiki - Keep in mind the wiki is a WIP and may not have information about everything in mcMMO!";

    private static final ConfigurationOptions LOADER_OPTIONS = ConfigurationOptions.defaults().setHeader(CONFIG_HEADER);
    
    private static final String ROOT_NODE_ADDRESS = "mcMMO";

    private final Path path;

    /**
     * The parent configuration - values are inherited from this
     */
    private final SerializedConfigLoader parent;

    /**
     * The loader (mapped to a file) used to read/write the config to disk
     */
    private HoconConfigurationLoader loader;

    /**
     * A node representation of "whats actually in the file".
     */
    private CommentedConfigurationNode fileData = SimpleCommentedConfigurationNode.root(LOADER_OPTIONS);

    /**
     * A node representation of {@link #fileData}, merged with the data of {@link #parent}.
     */
    private CommentedConfigurationNode data = SimpleCommentedConfigurationNode.root(LOADER_OPTIONS);

    /**
     * The mapper instance used to populate the config instance
     */
    private ObjectMapper<T>.BoundInstance configMapper;

    public SerializedConfigLoader(Class<T> clazz, String fileName, SerializedConfigLoader parent) {
        this.parent = parent;
        this.path = getPathFromFileName(fileName);

        try {
            Files.createDirectories(path.getParent());
            if (Files.notExists(path)) {
                Files.createFile(path);
            }

            this.loader = HoconConfigurationLoader.builder().setPath(path).build();
            this.configMapper = ObjectMapper.forClass(clazz).bindToNew();

            reload();
            save();
        } catch (Exception e) {
            mcMMO.p.getLogger().severe("Failed to initialize config - "+path.toString());
            e.printStackTrace();
        }
    }

    private Path getPathFromFileName(String fileName)
    {
        File configFile = new File(ConfigConstants.getConfigFolder(), fileName);
        return configFile.toPath();
    }

    public T getConfig() {
        return this.configMapper.getInstance();
    }

    public boolean save() {
        try {
            // save from the mapped object --> node
            CommentedConfigurationNode saveNode = SimpleCommentedConfigurationNode.root(LOADER_OPTIONS);
            this.configMapper.serialize(saveNode.getNode(ROOT_NODE_ADDRESS));

            // before saving this config, remove any values already declared with the same value on the parent
            if (this.parent != null) {
                removeDuplicates(saveNode);
            }

            // merge the values we need to write with the ones already declared in the file
            saveNode.mergeValuesFrom(this.fileData);

            // save the data to disk
            this.loader.save(saveNode);
            return true;
        } catch (IOException | ObjectMappingException e) {
            mcMMO.p.getLogger().severe("Failed to save configuration - "+path.toString());
            e.printStackTrace();
            return false;
        }
    }

    public void reload() {
        try {
            // load settings from file
            CommentedConfigurationNode loadedNode = this.loader.load();

            // store "what's in the file" separately in memory
            this.fileData = loadedNode;

            // make a copy of the file data
            this.data = this.fileData.copy();

            // merge with settings from parent
            if (this.parent != null) {
                this.parent.reload();
                this.data.mergeValuesFrom(this.parent.data);
            }

            // populate the config object
            populateInstance();
        } catch (Exception e) {
            mcMMO.p.getLogger().severe("Failed to load configuration - "+path.toString());
            e.printStackTrace();
        }
    }

    private void populateInstance() throws ObjectMappingException {
        this.configMapper.populate(this.data.getNode(ROOT_NODE_ADDRESS));
    }

    /**
     * Traverses the given {@code root} config node, removing any values which
     * are also present and set to the same value on this configs "parent".
     *
     * @param root The node to process
     */
    private void removeDuplicates(CommentedConfigurationNode root) {
        if (this.parent == null) {
            throw new IllegalStateException("parent is null");
        }

        Iterator<ConfigurationNodeWalker.VisitedNode<CommentedConfigurationNode>> it = ConfigurationNodeWalker.DEPTH_FIRST_POST_ORDER.walkWithPath(root);
        while (it.hasNext()) {
            ConfigurationNodeWalker.VisitedNode<CommentedConfigurationNode> next = it.next();
            CommentedConfigurationNode node = next.getNode();

            // remove empty maps
            if (node.hasMapChildren()) {
                if (node.getChildrenMap().isEmpty()) {
                    node.setValue(null);
                }
                continue;
            }

            // ignore list values
            if (node.getParent() != null && node.getParent().getValueType() == ValueType.LIST) {
                continue;
            }

            // if the node already exists in the parent config, remove it
            CommentedConfigurationNode parentValue = this.parent.data.getNode(next.getPath().getArray());
            if (Objects.equals(node.getValue(), parentValue.getValue())) {
                node.setValue(null);
            }
        }
    }

    public CommentedConfigurationNode getRootNode() {
        return this.data.getNode(ROOT_NODE_ADDRESS);
    }

    public Path getPath() {
        return this.path;
    }
}
