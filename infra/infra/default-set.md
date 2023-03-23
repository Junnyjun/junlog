# Default set

UPDATE

```bash
> sudo apt-get update -y
> sudo apt-get upgrade -y
> sudo apt-get install -y net-tools
```

TIME ZONE

<pre class="language-bash"><code class="lang-bash">> sudo timedatectl set-timezone Asia/Seoul
> sudo timedatectl set-local-rtc no
> sudo timedatectl set-ntp yes

> sudo apt install -y tzdata
<strong>> ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
</strong></code></pre>

LANG

```bash
> sudo apt-get install locales
> sudo dpkg-reconfigure locales
> sudo locale-gen ko_KR.UTF-8
> sudo update-locale LC_ALL=ko_KR.UTF-8 LANG=ko_KR.UTF-8
> export LANG=ko_KR.UTF-8
> export LC_ALL=ko_KR.UTF-8
```

VI

```bash
> echo "set hlsearch
set nu 
set autoindent 
set scrolloff=2
set wildmode=longest,list
set ts=4
set sts=4
set sw=1 
set autowrite
set autoread
set cindent
set bs=eol,start,indent
set history=256
set laststatus=2 
set paste
set shiftwidth=4
set showmatch 
set smartcase
set smarttab
set smartindent
set softtabstop=4
set tabstop=4
set ruler 
set incsearch
set statusline=\ %<%l:%v\ [%P]%=%a\ %h%m%r\ %F\

if $LANG[0]=='k' && $LANG[1]=='o'
set fileencoding=korea
endif

if has(\"syntax\")
 syntax on
endif

colorscheme jellybeans" >> ~/.vimrc
```
