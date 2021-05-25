package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import java.util.Random;



public class Birds extends ApplicationAdapter {
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;

	Texture obstaculoBaixo;
	Texture obstaculoCima;
	Texture passaro;
	Texture fundo;
	//float posicaox2=0;
	float posicaox=0;
	float posicaoY;
	float posicaoXpassaro = 0;
	float altura;
	float vao;

	float velocidade;
	float gravidade=2;

	Random alturaRandom;
	Circle circuloPassaro;
	Rectangle retanguloCima;
	Rectangle retanguloBaixo;

	int pontos = 0;
	boolean marcouPonto;
	int estadoJogo = 0;

	BitmapFont textoPontuacao;
	BitmapFont textoGameOver;

	Sound somVoa;
	Sound somHit;
	Sound somScore;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer=new ShapeRenderer();
		alturaRandom = new Random();

		obstaculoBaixo = new Texture("canobaixo.png");
		obstaculoCima = new Texture("canocima.png");
		passaro = new Texture("passaro.png");
		fundo =  new Texture("fundo.png");

		circuloPassaro = new Circle();
		retanguloBaixo = new Rectangle();
		retanguloCima = new Rectangle();

		posicaox=Gdx.graphics.getWidth();
		posicaoY=Gdx.graphics.getHeight()/2;
		altura=Gdx.graphics.getHeight()/2;
		vao=300;

		textoPontuacao = new BitmapFont();
		textoPontuacao.setColor(Color.WHITE);
		textoPontuacao.getData().setScale(10);

		textoGameOver = new BitmapFont();
		textoGameOver.setColor(Color.GREEN);
		textoGameOver.getData().setScale(2);

		somVoa = Gdx.audio.newSound(Gdx.files.internal("somVoa.mp3"));
		somHit = Gdx.audio.newSound(Gdx.files.internal("somHit.mp3"));
		somScore = Gdx.audio.newSound(Gdx.files.internal("somScore.mp3"));
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);
		/* Normalização do Tempo*/
		if(estadoJogo==0) {
			if (Gdx.input.justTouched()) {
				velocidade = -30;
				estadoJogo = 1;
				somVoa.play();
			}
		}else if(estadoJogo==1){
			posicaox-=Gdx.graphics.getDeltaTime()*500;

			if(posicaox < -obstaculoBaixo.getWidth()){
				posicaox = Gdx.graphics.getWidth();

				altura = Gdx.graphics.getHeight()/2;
				altura -= (alturaRandom.nextFloat() - 0.5f) * 150;
				marcouPonto=false;
			}

			if (Gdx.input.justTouched()) {
				velocidade = -30;
				somVoa.play();
			}

		if(posicaoY>0||velocidade<0){
			velocidade = velocidade+gravidade;
			posicaoY -=velocidade*Gdx.graphics.getDeltaTime()*30;
		}

		if(Gdx.input.justTouched()){
			velocidade = -30;
			somVoa.play();
		}
		}else if(estadoJogo==2){
			posicaoXpassaro -= Gdx.graphics.getDeltaTime()*500;
			if (Gdx.input.justTouched()) {
				estadoJogo = 0;
				pontos = 0;
				velocidade = 0;
				posicaoXpassaro = 0;
				marcouPonto = false;
				posicaox = Gdx.graphics.getWidth();
				posicaoY = Gdx.graphics.getHeight() / 2 - passaro.getHeight() / 2;
			}

		}

		if (posicaox < 50) {
			if(!marcouPonto){
				pontos++;
				marcouPonto=true;
				somScore.play();
			}
		}




		batch.begin();
		batch.draw(fundo, 0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getHeight());
		batch.draw(obstaculoBaixo, posicaox, altura-vao/2- obstaculoBaixo.getHeight());
		batch.draw(obstaculoCima, posicaox, altura+vao/2);
		batch.draw(passaro, 50, posicaoY);
		textoPontuacao.draw(batch, String.valueOf(pontos), Gdx.graphics.getWidth() / 2-20, Gdx.graphics.getHeight() - 110);

		if (estadoJogo==2){
			textoGameOver.draw(batch, "Toque para reiniciar!", Gdx.graphics.getWidth()/2 - 110, Gdx.graphics.getHeight()/2);
		}
		batch.end();

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.RED);
		retanguloCima = new Rectangle(posicaox, altura+vao, obstaculoBaixo.getWidth(), obstaculoCima.getHeight());
		retanguloBaixo = new Rectangle(posicaox, altura- obstaculoBaixo.getHeight()-vao,obstaculoBaixo.getWidth(), obstaculoBaixo.getHeight());
		circuloPassaro.set(50 + posicaoXpassaro + passaro.getWidth() / 2, posicaoY + passaro.getWidth() / 2, passaro.getHeight() / 2);
		if (Intersector.overlaps(circuloPassaro, retanguloCima) || Intersector.overlaps(circuloPassaro, retanguloBaixo)) {
			Gdx.app.log("meuLog", "Colidiu!");

			if (estadoJogo==1) {
				somHit.play();
				estadoJogo=2;
			}
		}
		shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		obstaculoBaixo.dispose();
	}
}
