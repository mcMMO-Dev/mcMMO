/*
 * This file is from mmoMinecraft (http://code.google.com/p/mmo-minecraft/).
 * 
 * mmoMinecraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gmail.nossr50.spout;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.*;

public class GenericLivingEntity extends GenericContainer {

	private Container _bars;
	private Container _space;
	private Label _label;
	private Gradient _health;
	private Gradient _armor;
	private GenericFace _face;
	private int health = 100;
	private int armor = 100;
	private int def_width = 80;
	private int def_height = 14;
	private boolean target = false;
	String face = "~";
	String label = "";

	public GenericLivingEntity() {
		super();
		Color black = new Color(0, 0, 0, 0.75f);

		this.addChildren( 
			new GenericContainer(	// Used for the bar, this.children with an index 1+ are targets
				_space = (Container) new GenericContainer()
					  .setMinWidth(def_width / 4)
					  .setMaxWidth(def_width / 4)
					  .setVisible(target),
				new GenericContainer(
					new GenericGradient()
							.setTopColor(black)
							.setBottomColor(black)
							.setPriority(RenderPriority.Highest),
					_bars = (Container) new GenericContainer(
						_health = (Gradient) new GenericGradient(),
						_armor = (Gradient) new GenericGradient()
					)		.setMargin(1)
							.setPriority(RenderPriority.High),
					new GenericContainer(
						_face = (GenericFace) new GenericFace()
								.setMargin(3, 0, 3, 3),
						_label = (Label) new GenericLabel()
								.setMargin(3)
					)		.setLayout(ContainerType.HORIZONTAL)
				)		.setLayout(ContainerType.OVERLAY)
			)		.setLayout(ContainerType.HORIZONTAL)
					.setMargin(0, 0, 1, 0)
					.setFixed(true)
					.setWidth(def_width)
					.setHeight(def_height)
		)		.setAlign(WidgetAnchor.TOP_LEFT)
				.setFixed(true)
				.setWidth(def_width)
				.setHeight(def_height + 1);

		this.setHealthColor(new Color(1f, 0, 0, 0.75f));
		this.setArmorColor(new Color(0.75f, 0.75f, 0.75f, 0.75f));
	}

	/**
	 * Set the display from a possibly offline player
	 * @param name
	 * @return 
	 */
	public GenericLivingEntity setEntity(String name) {
		return setEntity(name, "");
	}

	/**
	 * Set the display from a possibly offline player
	 * @param name
	 * @param prefix Place before the name
	 * @return 
	 */
	public GenericLivingEntity setEntity(String name, String prefix) {
		Player player = Bukkit.getServer().getPlayer(name);
		if (player != null && player.isOnline()) {
			return setEntity(player, prefix);
		}
		setHealth(0);
		setArmor(0);
		setLabel((!"".equals(prefix) ? prefix : "") + mmoHelper.getColor(screen != null ? screen.getPlayer() : null, null) + name);
		setFace("~" + name);
		return this;
	}

	/**
	 * Set the display from a player or living entity
	 * @param entity
	 * @return 
	 */
	public GenericLivingEntity setEntity(LivingEntity entity) {
		return setEntity(entity, "");
	}

	/**
	 * Set the display from a player or living entity
	 * @param entity
	 * @param prefix Place before the name
	 * @return 
	 */
	public GenericLivingEntity setEntity(LivingEntity entity, String prefix) {
		if (entity != null && entity instanceof LivingEntity) {
			setHealth(mmoHelper.getHealth(entity)); // Needs a maxHealth() check
			setArmor(mmoHelper.getArmor(entity));
			setLabel((!"".equals(prefix) ? prefix : "") + mmoHelper.getColor(screen != null ? screen.getPlayer() : null, entity) + mmoHelper.getSimpleName(entity, !target));
			setFace(entity instanceof Player ? ((Player)entity).getName() : "");
		} else {
			setHealth(0);
			setArmor(0);
			setLabel("");
			setFace("");
		}
		return this;
	}

	/**
	 * Set the targets of this entity - either actual targets, or pets etc
	 * @param targets
	 * @return 
	 */
	public GenericLivingEntity setTargets(LivingEntity... targets) {
		Widget[] widgets = this.getChildren();
		if (targets == null) {
			targets = new LivingEntity[0]; // zero-length array is easier to handle
		}
		for (int i=targets.length + 1; i<widgets.length; i++) {
			this.removeChild(widgets[i]);
		}
		for (int i=0; i<targets.length; i++) {
			GenericLivingEntity child;
			if (widgets.length > i + 1) {
				child = (GenericLivingEntity) widgets[i+1];
			} else {
				this.addChild(child = new GenericLivingEntity());
			}
			child.setTarget(true);
			child.setEntity(targets[i]);
		}
		setHeight((targets.length + 1) * (def_height + 1));
		updateLayout();
		return this;
	}

	public GenericLivingEntity setTarget(boolean target) {
		if (this.target != target) {
			this.target = target;
			_space.setVisible(target);
			updateLayout();
		}
		return this;
	}

	public GenericLivingEntity setHealth(int health) {
		if (this.health != health) {
			this.health = health;
			updateLayout();
		}
		return this;
	}

	public GenericLivingEntity setHealthColor(Color color) {
		_health.setTopColor(color).setBottomColor(color);
		return this;
	}

	public GenericLivingEntity setArmor(int armor) {
		if (this.armor != armor) {
			this.armor = armor;
			updateLayout();
		}
		return this;
	}

	public GenericLivingEntity setArmorColor(Color color) {
		_armor.setTopColor(color).setBottomColor(color);
		return this;
	}

	public GenericLivingEntity setLabel(String label) {
		if (!this.label.equals(label)) {
			this.label = label;
			_label.setText(label).setDirty(true);
			updateLayout();
		}
		return this;
	}

	public GenericLivingEntity setFace(String name) {
		if (!this.face.equals(name)) {
			this.face = name;
			_face.setVisible(!name.isEmpty());
			_face.setName(name);
			updateLayout();
		}
		return this;
	}

	@Override
	public Container updateLayout() {
		super.updateLayout();
		_armor.setWidth((_bars.getWidth() * armor) / 100).setDirty(true);
		_health.setWidth((_bars.getWidth() * health) / 100).setDirty(true);
		return this;
	}
}