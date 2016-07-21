package me.Cutiemango.MangoQuest.questobjects;

import net.md_5.bungee.api.chat.TextComponent;

public abstract class SimpleQuestObject {
	
	public abstract TextComponent toTextComponent(boolean isFinished);

	public abstract String toPlainText();

	private String startMessage = null;
	private String finishMessage = null;

	public String getStartMessage() {
		return (startMessage == null) ? startMessage : null;
	}

	public void setStartMessage(String msg) {
		this.startMessage = msg;
	}

	public String getFinishMessage() {
		return (finishMessage == null) ? finishMessage : null;
	}

	public void setFinishMessage(String msg) {
		this.finishMessage = msg;
	}

	public boolean hasStartMessage() {
		return (this.startMessage != null);
	}

	public boolean hasFinishMessage() {
		return (this.finishMessage != null);
	}

}
