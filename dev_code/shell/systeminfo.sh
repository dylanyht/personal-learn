#!/bin/bash
#
#********************************************************************
#Author:		Ronald
#Date: 			2020-10-15
#FileName：		deploy.sh
#Description：		A key deployment
#Copyright (C): 	2020 All rights reserved
#********************************************************************


#主机名
HOSTNAME=""

#PS1 Config		
PS_config="PS1='\[\e[0;36m\][\u@\H \w \t]$ \[\e[0m\]'"

#history_config
history_config='export HISTTIMEFORMAT="[%Y.%m.%d %H:%M:%S-$USER_IP-$USER]"'

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
SKYBLUE='\033[0;36m'
PLAIN=' \033[0m'

#样式
next() {
	printf "%-74s\n" "-" | sed 's/\s/-/g'
}

#关闭SELINUX和防火墙
System_init-Disabled_firewall() {
	if [ $(getenforce) != "Enforcing" ]; then
		return 0
		#echo -e "${SKYBLUE}SELINUX Pass${PLAIN}"
	else
		sed -i 's/SELINUX=enforcing/SELINUX=disabled/' /etc/selinux/config
		$(setenforce 0)  >>/dev/null 2>&1
	fi
	firewall_stat=$(systemctl status firewalld | grep Active | awk '{print $2}')
	if [ $firewall_stat == "active" ]; then
		systemctl disable --now firewalld >>/dev/null 2>&1
		return 0
		echo -e "${GREEN}Firewall set successfully${PLAIN}"
	else
		return 0
		#echo -e "${SKYBLUE}Firewall Pass${PLAIN}"
	fi
}

#更换YUM源为阿里云的源
System_init-Change_yum() {
	grep "https://mirrors.aliyun.com/" /etc/yum.repos.d/CentOS-Base.repo >>/dev/null
	if [ $? -ne 0 ]; then
		opsys=`awk -F"[ .]" '{print $4}' /etc/redhat-release`
		if [ $opsys -eq "7" ]; then
			mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup
			curl -so /etc/yum.repos.d/CentOS-Base.repo https://mirrors.aliyun.com/repo/Centos-7.repo
		elif [ $opsys -eq "8" ];then
			mv /etc/yum.repos.d/CentOS-Base.repo{,.backup} >>/dev/null
			mv /etc/yum.repos.d/CentOS-AppStream.repo{,.backup} >>/dev/null
			mv /etc/yum.repos.d/CentOS-centosplus.repo{,.backup} >>/dev/null
			mv /etc/yum.repos.d/CentOS-Extras.repo{,.backup} >>/dev/null
			mv /etc/yum.repos.d/CentOS-PowerTools.repo{,.backup} >>/dev/null
			curl -so /etc/yum.repos.d/CentOS-Base.repo https://mirrors.aliyun.com/repo/Centos-8.repo
		fi
		if [ $? -eq 0 ]; then
			yum makecache >>/dev/null
			sed -i 's/gpgcheck=1/gpgcheck=0/' /etc/yum.repos.d/CentOS-Base.repo
			echo -e "${GREEN}Aliyunal source set successfully${PLAIN}"
		fi
	else
		return 0
		#echo -e "${SKYBLUE}Yum Pass${PLAIN}"

	fi
}

#安装一些基础的包 如ifconfig vim wget bash-completion
System_init-Insstall_package() {
	if [ ! -e /usr/bin/wget ]; then
		yum install -y vim net-tools wget lrzsz bash-completion
		source /usr/share/bash-completion/bash_completion
	fi
}

#设置时区为中国CST
System_init-Change_timezone() {
	date_zone=$(date | awk -F" " '{print $5}')
	if [ $date_zone != "CST" ]; then
		timedatectl set-timezone Asia/Shanghai
		return 0
		echo -e "${GREEN}TimeZone set successfully${PLAIN}"
	else
		return 0
		#echo -e "${SKYBLUE}TimeZone Pass${PLAIN}"
	fi
}

#设置语言为en
System_init-Change_language() {
	grep 'LANG="en_US.UTF-8"' /etc/locale.conf >>/dev/null
	if [ $? -ne 0 ]; then
		sed -i 's/LANG.*/LANG="zh_CN.UTF-8"/' >>/etc/locale.conf
	else
		return 0
		#echo -e "${SKYBLUE}Language Pass${PLAIN}"
	fi
}

#设置命令提示符显示主机全称
System_init-Change_PS1() {
	if [ -e /home/${USERNAME}/.bashrc ]; then
		grep "PS1" /home/${USERNAME}/.bashrc >>/dev/null
		if [ $? -ne 0 ]; then
			echo ${PS_config} >>/home/${USERNAME}/.bashrc
			echo -e "${GREEN}Change PS1 successfully${PLAIN}"
		else
			return 0
			#echo -e "${SKYBLUE}PS1 Pass${PLAIN}"
		fi
	fi
}

System_init-Add_historical_time() {
	grep "HISTTIMEFORMAT" /etc/profile >>/dev/null
	if [ $? -ne 0 ]; then
		echo ${history_config} >>/etc/profile
		source /etc/profile
		echo -e "${GREEN}Add historical time successfully${PLAIN}"
	else
		return 0
		#echo -e "${SKYBLUE}Add historical time Pass${PLAIN}"
	fi
}

#设置主机名
System_init-Change_hostname() {
	if [[ -n ${HOSTNAME} ]]; then
		if [ $(hostname) == "localhost.localdomain" ]; then
			hostnamectl set-hostname ${HOSTNAME} \
			&& echo -e "${GREEN}Hostname updated successfully${PLAIN}"
		else
			return 0
			#echo -e "${SKYBLUE}Hostname Pass${PLAIN}"
		fi
	else
		return 0
		#echo -e "${RED}ERR:Hostname Parameter is null${PLAIN}"
	fi
}

#禁止root远程登陆
System_init-Not_allow_login() {
	grep "PermitRootLogin yes" /etc/ssh/sshd_config >>/dev/null
	if [ $? -eq 0 ]; then
		sed -i 's/PermitRootLogin yes/PermitRootLogin no/' /etc/ssh/sshd_config >>/dev/null
		systemctl restart sshd
		echo -e "${GREEN}Not allow login for root successfully${PLAIN}"
	else
		echo -e "${SKYBLUE}Not allow login Pass${PLAIN}"
	fi

}

#设置系统基础信息
System_init() {
	System_init-Disabled_firewall
	System_init-Change_yum
	System_init-Insstall_package
	System_init-Change_timezone
	System_init-Change_language
	System_init-Change_PS1
	System_init-Add_historical_time
	System_init-Change_hostname
	next
	echo -e "${GREEN}Infrastructure init successfully ${PLAIN}"
	next
}

#安装docker及swarm集群
Install_Docker() {
	yum remove docker \
		docker-client \
		docker-client-latest \
		docker-common \
		docker-latest \
		docker-latest-logrotate \
		docker-logrotate \
		docker-engine -y >>/dev/null 2>&1
	which docker >>/dev/null 2>&1
	if [ $? -ne 0 ]; then
		yum install -y yum-utils >>/dev/null
		yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
		#yum install -y https://mirrors.aliyun.com/docker-ce/linux/centos/7/x86_64/stable/Packages/containerd.io-1.3.7-3.1.el8.x86_64.rpm
		yum install -y https://mirrors.aliyun.com/docker-ce/linux/centos/8/x86_64/stable/Packages/containerd.io-1.4.3-3.1.el8.x86_64.rpm
		#yum install -y https://mirrors.aliyun.com/docker-ce/linux/centos/7/x86_64/stable/Packages/containerd.io-1.2.13-3.2.el7.x86_64.rpm
		curl -so docker.gpg https://mirrors.aliyun.com/docker-ce/linux/centos/gpg
		rpmkeys --import docker.gpg
		rm -f docker.gpg
		yum install docker-ce docker-ce-cli containerd.io -y
		if [ $? -eq 0 ]; then
			systemctl enable --now docker >>/dev/null 2>&1
			docker swarm init --advertise-addr `hostname -I|awk '{print $1}'` >>/dev/null
			docker swarm update --task-history-limit 0 >>/dev/null
			usermod -aG docker ${USERNAME}
			echo "{}" | docker config create ocelot.json -
			echo -e "${GREEN}Docker install successfully ${PLAIN}"
		fi
		if [ ! -s /etc/docker/daemon.json ]; then
			echo "{" >/etc/docker/daemon.json
			echo '  "registry-mirrors": ["https://vsqtvftm.mirror.aliyuncs.com"] ' >>/etc/docker/daemon.json
			echo "}" >>/etc/docker/daemon.json
			systemctl daemon-reload
			systemctl restart docker
		fi
	else
		echo -e "${SKYBLUE}Docker Pass${PLAIN}"
	fi
}


if [ $# -eq 0 ] ; then
	#获取系统信息并展示出来
	Check_sysinfo() {

		# Check if user is root
		[ $(id -u) != "0" ] && {
			echo -e "${RED}Error: You must be root to run this script${PLAIN}"
			exit 1
		}

		if [ -z "$outnet_ip" ]; then
			outnet_ip=$(curl --connect-timeout 3 -s http://pv.sohu.com/cityjson 2>>/dev/null | awk -F '"' '{print $4}')
		fi

		# Check if wget installed
		if [ ! -e '/usr/sbin/ifconfig' ]; then
			yum install net-tools -y >>/dev/null
		fi

		get_opsy() {
			[ -f /etc/redhat-release ] && awk '{print ($1,$3~/^[0-9]/?$3:$4)}' /etc/redhat-release && return
			[ -f /etc/os-release ] && awk -F'[= "]' '/PRETTY_NAME/{print $3,$4,$5}' /etc/os-release && return
			[ -f /etc/lsb-release ] && awk -F'[="]+' '/DESCRIPTION/{print $2}' /etc/lsb-release && return
		}

		Calc_disk() {
			local total_size=0
			local array=$@
			for size in ${array[@]}; do
				[ "${size}" == "0" ] && size_t=0 || size_t=$(echo ${size:0:${#size}-1})
				[ "$(echo ${size:(-1)})" == "K" ] && size=0
				[ "$(echo ${size:(-1)})" == "M" ] && size=$(awk 'BEGIN{printf "%.1f", '$size_t' / 1024}')
				[ "$(echo ${size:(-1)})" == "T" ] && size=$(awk 'BEGIN{printf "%.1f", '$size_t' * 1024}')
				[ "$(echo ${size:(-1)})" == "G" ] && size=${size_t}
				total_size=$(awk 'BEGIN{printf "%.1f", '$total_size' + '$size'}')
			done
			echo ${total_size}
		}

		# Vars

		model=$(dmidecode | grep "Product Name" | head -1 | tr -d " " | awk -F: '{print $2}')
		cname=$(awk -F: '/model name/ {name=$2} END {print name}' /proc/cpuinfo | sed 's/^[ \t]*//;s/[ \t]*$//')
		cores=$(cat /proc/cpuinfo | grep "physical id" | sort | uniq | wc -l)
		ucores=$(grep 'core id' /proc/cpuinfo | sort -u | wc -l)
		thread=$(awk -F: '/model name/ {processor++} END {print processor}' /proc/cpuinfo)
		freq=$(awk -F: '/cpu MHz/ {freq=$2} END {print freq}' /proc/cpuinfo | sed 's/^[ \t]*//;s/[ \t]*$//')
		freq=$(awk -F: '/cpu MHz/ {freq=$2} END {print freq}' /proc/cpuinfo | sed 's/^[ \t]*//;s/[ \t]*$//')
		tram=$(free -h | awk '/Mem/ {print $2}')
		fram=$(free -h | awk '/Mem/ {print $4}')
		swap=$(free -h | awk '/Swap/ {print $2}')
		fswap=$(free -h | awk '/Swap/ {print $4}')
		up=$(awk '{a=$1/86400;b=($1%86400)/3600;c=($1%3600)/60} {printf("%d days, %d hour %d min\n",a,b,c)}' /proc/uptime)
		load=$(w | head -1 | awk -F'load average:' '{print $2}' | sed 's/^[ \t]*//;s/[ \t]*$//')
		opsy=$(get_opsy)
		arch=$(uname -m)
		lbit=$(getconf LONG_BIT)
		kern=$(uname -r)
		disk_size1=($(LANG=C df -hPl | grep -wvE '\-|none|tmpfs|devtmpfs|by-uuid|chroot|Filesystem' | awk '{print $2}'))
		disk_size2=($(LANG=C df -hPl | grep -wvE '\-|none|tmpfs|devtmpfs|by-uuid|chroot|Filesystem' | awk '{print $4}'))
		disk_total_size=$(Calc_disk ${disk_size1[@]})
		disk_free_size=$(Calc_disk ${disk_size2[@]})
		date_zone=$(date | awk -F" " '{print $5}')
		net_name=$(/sbin/ifconfig | grep -a1 -e "[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}" | grep ^e | awk -F: '{print $1}' | head -1)
		net_path=/etc/sysconfig/network-scripts/ifcfg-$net_name
		net_status=$(cat $net_path | grep BOOTPROTO | awk -F"=" '{print $2}')
		net_ip=$(/sbin/ifconfig | grep -A 1 ^e | grep -e "[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}" | awk '{print $2}' | head -1)
		net_mask=$(/sbin/ifconfig | grep -A 1 ^e[a-z][a-z][0-9] | grep netmask | awk '{print $4}' | head -1)
		net_gateway=$(route -n | sed -n '3p' | awk '{print $2}')
		net_dns1=$(grep nameserver /etc/resolv.conf | sed -n '1p' | awk '{print $2}')
		net_dns2=$(grep nameserver /etc/resolv.conf | sed -n '2p' | awk '{print $2}')
		
		clear
		echo "                                  SystemInfo                                     "
		next
		echo -e "Model                : ${SKYBLUE}$model${PLAIN}"
		echo -e "CPUModel Name        : ${SKYBLUE}$cname${PLAIN}"
		echo -e "CPUPhysicalNo        : ${SKYBLUE}$cores"cores"${PLAIN}"
		echo -e "CPUCores             : ${SKYBLUE}$ucores"cores"${PLAIN}"
		echo -e "CPUThread            : ${SKYBLUE}$thread${PLAIN}"
		echo -e "CPUFrequency         : ${SKYBLUE}$freq MHz${PLAIN}"
		echo -e "Hostname             : ${SKYBLUE}$(hostname)${PLAIN}"
		echo -e "HardDiskSize         : ${SKYBLUE}$disk_total_size GB (Avail $disk_free_size GB)${PLAIN}"
		echo -e "RootDirectorySize    : ${SKYBLUE}$disk_size1 (Avail $disk_size2 )${PLAIN}"
		echo -e "Mem                  : ${SKYBLUE}$tram (Avail $fram)${PLAIN}"
		echo -e "Swap                 : ${SKYBLUE}$swap (Avail $fswap)${PLAIN}"
		echo -e "RunningTime          : ${SKYBLUE}$up${PLAIN}"
		echo -e "SystemLoad           : ${SKYBLUE}$load${PLAIN}"
		echo -e "OperatingSystem      : ${SKYBLUE}$opsy${PLAIN}"
		echo -e "Kernel               : ${SKYBLUE}$kern${PLAIN}"
		echo -e "SystemPlatform       : ${SKYBLUE}$arch ($lbit "Bit")${PLAIN}"
		echo -e "Date                 : ${SKYBLUE}$(date '+%F %R') ${PLAIN}"
		echo -e "TimeZone             : ${SKYBLUE}$date_zone${PLAIN}"
		echo -e "Language             : ${SKYBLUE}$(echo $LANG)${PLAIN}"
		echo -e "IPstatus             : ${SKYBLUE}$net_status${PLAIN}"
		echo -e "InternetIP           : ${SKYBLUE}$outnet_ip${PLAIN}"
		echo -e "PrivateIP            : ${SKYBLUE}$net_ip${PLAIN}"
		echo -e "Netmask              : ${SKYBLUE}$net_mask${PLAIN}"
		echo -e "Gateway              : ${SKYBLUE}$net_gateway${PLAIN}"
		echo -e "DNS1                 : ${SKYBLUE}$net_dns1${PLAIN}"
		echo -e "DNS2                 : ${SKYBLUE}$net_dns2${PLAIN}"
	}
clear
Check_sysinfo
next
echo -e "1. One Key Init Seting
2. One Key Install Docker"
next
read -e -p "Input Number[1-2](Default Q quit):" num
	case "$num" in
		1)
			System_init
			#   Change_network
			if [ $NotAllowLogin -eq 1 ]; then
				echo "${GREEN} Not_allow_login Execute successfully ${PLAIN}"
			fi
			;;
		2)
			Install_Docker
			if [ $NotAllowLogin -eq 1 ]; then
				echo "${GREEN} Not_allow_login Execute successfully ${PLAIN}"
			fi
			;;
		q | Q)
			exit
			;;
		*)
			echo -e "${Error} Please enter valid number [1-3]"
			;;
	esac
else
while [[ $# -gt 0 ]] ;do
	case $1 in
	-h|--help)
		echo "bash ${BASH_SOURCE} [OPTION] ...
脚本可以携带多个参数。
	参数说明：
-init:
	初始化Centos操作系统如更换yum源，添加常用包，关闭防火墙等。
-docker：
	初始化操作系统后，安装docker
-history：
	设置历史记录显示格式
-yum：
	更换主机YUM源为阿里云
		"
	exit 0;;
	-init)
		System_init
		if [ $NotAllowLogin -eq 1 ]; then
			echo "${GREEN} Not_allow_login Execute successfully ${PLAIN}"
		fi
	shift	
	;;
	-docker)
		System_init
		Install_MES-Install_docker
		if [ $NotAllowLogin -eq 1 ]; then
				echo "${GREEN} Not_allow_login Execute successfully ${PLAIN}"
		fi
	shift	
	;;
	-hostname)
		System_init-Change_hostname
	shift
	;;
	-history)
		System_init-Add_historical_time
	shift
	;;
	-yum)
		System_init-Change_yum
	shift
	;;
	*)
	echo "参数错误"
	exit 1
	;;
esac
done
fi
