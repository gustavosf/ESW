# encoding: utf-8

class UserController < ApplicationController

  # def new
  # 	print "kdoekdoeakdoea"
  # end

  def index
    @users = User.all
  end

  def show
    @user = User.find params[:id]
  end
  
  def update
    user = User.find params[:id]
    user.name = params[:user][:name]
    user.email = params[:user][:email]
    user.access = params[:user][:access]
    user.save
    redirect_to user_index_path, notice: "O usuário #{user.name} foi atualizado com sucesso!"
  end

  def create
    if User.where(email: params[:user][:email]).exists? then
      flash[:error] = "O e-mail #{params[:user][:email]} já está cadastrado no sistema!"
      redirect_to new_user_path, error: "O e-mail #{params[:user][:email]} já está cadastrado no sistema!" 
      return
    end

    user = User.new
    user.name = params[:user][:name]
    user.email = params[:user][:email]
    user.access = params[:user][:access]
    user.save
    redirect_to user_index_path, notice: "O usuário #{user.name} foi cadastrado com sucesso!"
  end
end